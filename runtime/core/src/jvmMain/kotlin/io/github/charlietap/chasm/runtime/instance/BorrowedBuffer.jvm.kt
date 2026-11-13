package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.host.ByteBufferHostMemory
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@OptIn(UnsafeHostApi::class)
internal actual fun borrowedBuffer(memory: LinearMemory): Any? {
    return (memory as? ByteBufferHostMemory)?.unsafeBorrowByteBuffer()
}
