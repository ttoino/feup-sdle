package pt.up.fe.sdle.cluster

import pt.up.fe.sdle.storage.StorageDriver

/**
 *
 */
class Node(
    /**
     * The storage driver responsible for storing data on this node
     */
    var storageDriver: StorageDriver,
    private var _peers: MutableList<String>,
) {
    /**
     *
     */
    val peers: List<String> get() = _peers.toList()

    constructor(driver: StorageDriver) : this(driver, mutableListOf())

    /**
     * Adds the given IP Address to this node's list of peers
     */
    fun addPeer(peer: String) = _peers.add(peer)
}
