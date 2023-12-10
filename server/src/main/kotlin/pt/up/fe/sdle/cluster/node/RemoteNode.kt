@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.up.fe.sdle.cluster.node

import pt.up.fe.sdle.crdt.ShoppingList
import pt.up.fe.sdle.storage.StorageKey
import java.util.*

/**
 * Represents a peer node in the context of this running instance.
 *
 * All calls result in a network RPC to the actual node that should handle the request.
 */
class RemoteNode(
    address: String = "0.0.0.0",
    id: NodeID = UUID.randomUUID().toString(),
) : Node(address, id) {
    override suspend fun bootstrap() {
        println("")
    }

    override suspend fun put(
        key: StorageKey,
        data: ShoppingList,
    ): ShoppingList {
        TODO("RPC to actual node")
    }

    override suspend fun get(key: StorageKey): ShoppingList? {
        TODO("RPC to actual node")
    }
}
