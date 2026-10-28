package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.host.ByteArrayHostMemory
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.MAX_PAGES
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import io.github.charlietap.chasm.runtime.memory.OutOfMemoryError
import io.github.charlietap.chasm.type.MemoryType
import kotlin.jvm.JvmField

data class MemoryInstance(
    var type: MemoryType,
    var data: LinearMemory,
    val maximumPages: Int = declaredMaximumPages(type),
) {
    var size: Int = size()
        private set

    var bounds: IntRange = bounds()
        private set

    @JvmField
    var byteArray: ByteArray? = byteArray()

    fun refresh() {
        size = size()
        bounds = bounds()
        byteArray = byteArray()
    }

    fun grow(pagesToAdd: Int): Int {
        val currentSize = type.limits.min.toInt()

        if (pagesToAdd == 0) {
            return currentSize
        }

        val newSize = currentSize + pagesToAdd

        if (pagesToAdd < 0 || newSize < currentSize || newSize > maximumPages) {
            return -1
        }

        val grown = try {
            data.grow(pagesToAdd)
        } catch (_: IllegalArgumentException) {
            return -1
        } catch (_: OutOfMemoryError) {
            return -1
        }

        data = grown
        type.limits.min = newSize.toULong()
        refresh()

        return currentSize
    }

    private fun size() = type.limits.min.toInt() * PAGE_SIZE

    private fun bounds() = 0 until size

    @OptIn(UnsafeHostApi::class)
    private fun byteArray() = (data as? ByteArrayHostMemory)?.unsafeBorrowByteArray()?.takeIf { array ->
        array.size.toLong() >= size.toLong() + REQUIRED_OVERREAD_BYTES
    }

    companion object {
        const val REQUIRED_OVERREAD_BYTES = 4

        fun declaredMaximumPages(type: MemoryType): Int =
            type.limits.max?.let { maximum -> minOf(maximum, MAX_PAGES.toULong()).toInt() } ?: MAX_PAGES
    }
}
