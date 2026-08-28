package io.github.charlietap.chasm.host

interface NativeHostMemory : HostMemory {

    /** Borrows the real array, which becomes stale when memory is replaced. */
    @UnsafeHostApi
    fun unsafeBorrowByteArray(): ByteArray
}
