package pt.up.fe.sdle.cluster.node.services.handoff

import pt.up.fe.sdle.cluster.node.Node
import pt.up.fe.sdle.crdt.ShoppingList

/**
 * Service that handles the process of hinted hand-offs for a given node.
 */
interface HintedHandoffService {
    /**
     * Stores a hint to be dispatched later.
     */
    suspend fun storeHint(hint: Hint)

    /**
     * Bootstraps this service.
     */
    suspend fun bootstrap()
}

/**
 * A hint to be used to put a value
 */
data class Hint(
    /**
     * The node to which this hint is directed at.
     */
    val node: Node,
    /**
     * The list data that should be sent to the hinted at node.
     */
    val list: ShoppingList,
    /**
     * Whether this hint was generated as part of a replication attempt or not.
     */
    val replica: Boolean = false,
)
