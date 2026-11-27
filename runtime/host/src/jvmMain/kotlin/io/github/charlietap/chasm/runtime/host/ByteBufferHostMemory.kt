package io.github.charlietap.chasm.runtime.host

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.host.UnsafeHostApi
import java.nio.ByteBuffer

@InternalChasmApi
interface ByteBufferHostMemory : HostMemory {

    /**
     * Borrows the current logical memory buffer. Use absolute access and do not
     * change its byte order, position, or limit. It becomes stale after growth.
     */
    @UnsafeHostApi
    fun unsafeBorrowByteBuffer(): ByteBuffer
}
