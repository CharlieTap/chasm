package io.github.charlietap.chasm.embedding.memory

import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.host.UnsafeHostApi
import java.nio.ByteBuffer

@UnsafeHostApi
fun unsafeBorrowMemoryBuffer(memory: Memory): ByteBuffer? {
    return memory.engine.unsafeBorrowMemory(memory) as? ByteBuffer
}
