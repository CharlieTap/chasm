package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.runtime.address.StoreIdentity
import io.github.charlietap.chasm.runtime.store.InstanceLifetimeRegistry

data class Heap(
    var sizeInBytes: Long = 0,
    val arrayReferencePool: ArrayDeque<Int> = ArrayDeque(),
    val structReferencePool: ArrayDeque<Int> = ArrayDeque(),
) {
    internal var instanceLifetimeRegistry: InstanceLifetimeRegistry? = null
    internal var storeIdentity: StoreIdentity? = null
}
