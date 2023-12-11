package pt.up.fe.sdle.cluster.node.services.gossip

import pt.up.fe.sdle.cluster.node.Node

/**
 * A service implementing a membership gossip-based protocol for nodes on the cluster.
 */
interface GossipProtocolService {
    /**
     * Initiates a gossip transaction with a peer node to exchange cluster status information.
     */
    suspend fun propagateClusterStatus(gossipNode: Node)
}
