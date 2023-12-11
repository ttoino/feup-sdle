package pt.up.fe.sdle.cluster.node.services.bootstrap

import pt.up.fe.sdle.cluster.node.services.Service

/**
 * A service implementing a bootstrapping process for nodes on this cluster.
 */
interface BootstrapService : Service {
    /**
     * Initiates a gossip transaction with a peer node to exchange cluster status information
     */
    suspend fun bootstrap()
}
