package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.host.HostReference
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

    override fun read(
        buffer: LongArray,
        elementIndex: Int,
        elementsToRead: Int,
        bufferIndex: Int,
    ): LongArray = elements.copyInto(
        destination = buffer,
        destinationOffset = bufferIndex,
        startIndex = elementIndex,
        endIndex = elementIndex + elementsToRead,
    )

    override fun write(
        elementIndex: Int,
        buffer: LongArray,
        bufferIndex: Int,
        elementsToWrite: Int,
    ) {
        buffer.copyInto(
            destination = elements,
            destinationOffset = elementIndex,
            startIndex = bufferIndex,
            endIndex = bufferIndex + elementsToWrite,
        )
    }

    override fun fill(
        elementIndex: Int,
        value: HostReference,
        elementsToFill: Int,
    ) {
        elements.fill(value, elementIndex, elementIndex + elementsToFill)
    }

    override fun copy(
        sourceElementIndex: Int,
        destinationElementIndex: Int,
        elementsToCopy: Int,
        source: HostTable,
    ) {
        source.unsafeBorrowElements().copyInto(
            destination = elements,
            destinationOffset = destinationElementIndex,
            startIndex = sourceElementIndex,
            endIndex = sourceElementIndex + elementsToCopy,
        )
    }

    override fun move(
        sourceElementIndex: Int,
        destinationElementIndex: Int,
        elementsToMove: Int,
        source: HostTable,
    ) {
        source.unsafeBorrowElements().copyInto(
            destination = elements,
            destinationOffset = destinationElementIndex,
            startIndex = sourceElementIndex,
            endIndex = sourceElementIndex + elementsToMove,
        )
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
