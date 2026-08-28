package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.host.HostTable
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.type.TableType

@OptIn(UnsafeHostApi::class)
data class TableInstance(
    var type: TableType,
    var elements: LongArray,
) : HostTable {

    override val size: Int
        get() = elements.size

    override fun readRaw(index: Int): Long = elements[index]

    override fun writeRaw(index: Int, value: Long) {
        elements[index] = value
    }

    @UnsafeHostApi
    override fun unsafeBorrowElements(): LongArray = elements

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as TableInstance

        if (type != other.type) return false
        if (!elements.contentEquals(other.elements)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + elements.contentHashCode()
        return result
    }
}
