package pt.up.fe.sdle.cluster.node.services.bootstrap

import pt.up.fe.sdle.logger

/**
 * Dummy [BootstrapService] that does nothing
 */
class DummyBootstrapService : BootstrapService {
    override suspend fun bootstrap() {
        logger.debug("Bootstrapping with dummy service does nothing")
    }
}