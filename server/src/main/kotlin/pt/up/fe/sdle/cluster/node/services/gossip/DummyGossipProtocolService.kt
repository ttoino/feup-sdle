package pt.up.fe.sdle.cluster.node.services.gossip

import pt.up.fe.sdle.cluster.node.Node

/**
 * Dummy implementation that does nothing.
 */
class DummyGossipProtocolService : GossipProtocolService {
    override suspend fun propagateClusterStatus(gossipNode: Node) {}
}
