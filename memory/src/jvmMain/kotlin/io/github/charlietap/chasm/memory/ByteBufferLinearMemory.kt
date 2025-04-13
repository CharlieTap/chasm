package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ByteBufferLinearMemory(
    var memory: ByteBuffer,
) : LinearMemory {
    constructor(
        pages: LinearMemory.Pages,
    ) : this(
        ByteBuffer.allocateDirect(pages.amount.toInt() * LinearMemory.PAGE_SIZE).apply {
            order(ByteOrder.LITTLE_ENDIAN)
        },
    )
}
