package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.memory.atomic.AtomicAccessors
import io.github.charlietap.chasm.memory.atomic.WaitRegistry
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ByteBufferLinearMemory(
    var memory: ByteBuffer,
    val waitRegistry: WaitRegistry = WaitRegistry(),
) : LinearMemory {

    constructor(
        pages: LinearMemory.Pages,
        aligned: Boolean = false,
    ) : this(
        if (aligned) {
            allocateAligned(pages.amount.toInt() * LinearMemory.PAGE_SIZE)
        } else {
            allocateUnaligned(pages.amount.toInt() * LinearMemory.PAGE_SIZE)
        },
    )

    companion object {
        private const val ALIGNMENT = 8

        private fun allocateAligned(size: Int): ByteBuffer {
            if (size == 0) {
                return ByteBuffer.allocateDirect(0).order(ByteOrder.LITTLE_ENDIAN)
            }

            val raw = ByteBuffer.allocateDirect(size + ALIGNMENT - 1)
            raw.order(ByteOrder.LITTLE_ENDIAN)

            for (offset in 0 until ALIGNMENT) {
                val slice = raw.position(offset).slice()
                slice.order(ByteOrder.LITTLE_ENDIAN)
                slice.limit(size)
                try {
                    AtomicAccessors.checkAlignment(slice)
                    return slice
                } catch (_: IllegalStateException) {
                    // Not aligned, try next offset
                }
            }

            // Fallback (should not happen with direct buffers)
            return raw.position(0).slice().order(ByteOrder.LITTLE_ENDIAN).also {
                it.limit(size)
            }
        }

        private fun allocateUnaligned(size: Int): ByteBuffer {
            return ByteBuffer.allocateDirect(size).order(ByteOrder.LITTLE_ENDIAN)
        }
    }
}
