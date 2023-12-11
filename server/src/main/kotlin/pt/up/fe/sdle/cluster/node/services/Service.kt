package pt.up.fe.sdle.cluster.node.services

import pt.up.fe.sdle.cluster.node.Node

/**
 * A service running in the context of a node.
 */
interface Service {

    /**
     * Initializes this service on the given node.
     *
     * @param node The node this service runs on
     */
    suspend fun init(node: Node)
}