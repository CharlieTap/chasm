package io.github.charlietap.chasm.host

import java.nio.ByteBuffer

interface ByteBufferHostMemory : HostMemory {

    /**
     * Borrows the backing buffer. Use absolute access and do not change its byte
     * order, position, or limit. It becomes stale when memory is replaced.
     */
    @UnsafeHostApi
    fun unsafeBorrowByteBuffer(): ByteBuffer
}
