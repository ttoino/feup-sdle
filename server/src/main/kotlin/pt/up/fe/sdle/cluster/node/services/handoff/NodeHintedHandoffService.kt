package pt.up.fe.sdle.cluster.node.services.handoff

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.up.fe.sdle.cluster.Cluster
import pt.up.fe.sdle.logger
import java.net.http.HttpConnectTimeoutException
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration.Companion.seconds

/**
 * Hinted Hand-off service for nodes.
 * TODO change KDocs
 */
class NodeHintedHandoffService : HintedHandoffService {
    private val hints: ConcurrentLinkedQueue<Hint> = ConcurrentLinkedQueue()

    override suspend fun storeHint(hint: Hint) {
        logger.info("Storing hint for data ")

        hints.add(hint)
    }

    override suspend fun bootstrap() {
        coroutineScope {
            launch {
                while (true) {
                    // Wait for some time before checking if there are any other hints available
                    delay(HINT_DELAY_S.seconds)

                    val hint = hints.poll()

                    if (hint === null) {
                        continue
                    }

                    val (node, list, replica) = hint

                    val nodeAddress = node.address
                    val listId = list.id

                    try {
                        node.put(listId, list, replica)

                        // TODO: we should merge so that we save a few steps later
                    } catch (e: Exception) {
                        when (e) {
                            is HttpConnectTimeoutException -> {
                                logger.warn(
                                    "Node @ $nodeAddress with id ${node.id} is still unreachable, hint re-queued to be sent later",
                                )
                            }

                            else -> {
                                logger.error("Unknown error", e)
                            }
                        }

                        // storeHint(hint) this is already handled by the node since it is (mostly if not every time) a sRemoteNode
                    }
                }
            }
        }
    }

    companion object {
        private val HINT_DELAY_S = Cluster.Config.Node.Services.HintedHandoff.HINT_DELAY_S
    }
}
