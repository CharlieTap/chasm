package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostGlobal
import io.github.charlietap.chasm.host.HostReference
import io.github.charlietap.chasm.host.HostTable
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.type.AbstractHeapType

class ManagedHostGlobal(
    private val bridge: ManagedReferenceBridge,
    private val global: ReferenceGlobal,
    private val extern: Boolean,
) : HostGlobal {

    override var rawValue: Long
        get() = bridge.toHost(global.value, extern)
        set(value) {
            global.value = bridge.fromHost(value)
        }
}

class ManagedHostTable(
    private val bridge: ManagedReferenceBridge,
    private val table: ReferenceTable,
) : HostTable {

    private val extern = table.instance.type.referenceType.heapType.let { heap -> heap == AbstractHeapType.Extern || heap == AbstractHeapType.NoExtern }

    override val size: Int
        get() = table.elements.size

    override fun readRaw(index: Int): Long {
        return bridge.toHost(table.elements[index], extern)
    }

    override fun writeRaw(
        index: Int,
        value: Long,
    ) {
        table.elements[index] = bridge.fromHost(value)
    }

    override fun read(
        buffer: LongArray,
        elementIndex: Int,
        elementsToRead: Int,
        bufferIndex: Int,
    ): LongArray {
        val elements = table.elements
        for (offset in 0 until elementsToRead) buffer[bufferIndex + offset] = bridge.toHost(elements[elementIndex + offset], extern)
        return buffer
    }

    override fun write(
        elementIndex: Int,
        buffer: LongArray,
        bufferIndex: Int,
        elementsToWrite: Int,
    ) {
        val elements = table.elements
        for (offset in 0 until elementsToWrite) elements[elementIndex + offset] = bridge.fromHost(buffer[bufferIndex + offset])
    }

    override fun fill(
        elementIndex: Int,
        value: HostReference,
        elementsToFill: Int,
    ) {
        table.elements.fill(bridge.fromHost(value), elementIndex, elementIndex + elementsToFill)
    }

    override fun copy(
        sourceElementIndex: Int,
        destinationElementIndex: Int,
        elementsToCopy: Int,
        source: HostTable,
    ) {
        move(sourceElementIndex, destinationElementIndex, elementsToCopy, source)
    }

    override fun move(
        sourceElementIndex: Int,
        destinationElementIndex: Int,
        elementsToMove: Int,
        source: HostTable,
    ) {

        if (source is ManagedHostTable) {
            source.table.elements.copyInto(table.elements, destinationElementIndex, sourceElementIndex, sourceElementIndex + elementsToMove)
            return
        }

        val buffer = source.read(LongArray(elementsToMove), sourceElementIndex, elementsToMove)
        write(destinationElementIndex, buffer, 0, elementsToMove)
    }

    @UnsafeHostApi
    override fun unsafeBorrowElements(): LongArray {
        throw UnsupportedOperationException("managed tables do not expose raw element storage")
    }
}
