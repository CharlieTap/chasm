package io.github.charlietap.chasm.runtime.host

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.host.UnsafeHostApi

@InternalChasmApi
interface ByteArrayHostMemory : HostMemory {

    /**
     * Borrows the backing array, which becomes stale when backing storage is replaced.
     * Only indices below [byteSize] are accessible; the array may contain spare
     * capacity that must not be read or written. Reacquire it after memory growth.
     */
    @UnsafeHostApi
    fun unsafeBorrowByteArray(): ByteArray
}
