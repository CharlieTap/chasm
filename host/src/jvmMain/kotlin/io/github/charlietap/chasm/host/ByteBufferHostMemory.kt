package io.github.charlietap.chasm.host

import java.nio.ByteBuffer

interface ByteBufferHostMemory : HostMemory {

    /**
     * Borrows the current logical memory buffer. Use absolute access and do not
     * change its byte order, position, or limit. It becomes stale after growth.
     */
    @UnsafeHostApi
    fun unsafeBorrowByteBuffer(): ByteBuffer
}
