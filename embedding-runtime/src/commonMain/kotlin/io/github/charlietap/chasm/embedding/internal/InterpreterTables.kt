package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.host.withScope

internal interface TableAccess {

    fun sizeTable(table: Table): Int

    fun growTable(
        table: Table,
        elementsToAdd: Int,
        initial: Any?,
    ): Int

    fun readTable(
        table: Table,
        index: Int,
    ): Any?

    fun readTableElements(
        table: Table,
        buffer: Array<Any?>,
        elementIndex: Int,
        elementsToRead: Int,
        bufferIndex: Int,
    ): Array<Any?>

    fun writeTable(
        table: Table,
        index: Int,
        value: Any?,
    )

    fun writeTableElements(
        table: Table,
        elementIndex: Int,
        buffer: Array<Any?>,
        bufferIndex: Int,
        elementsToWrite: Int,
    )

    fun fillTable(
        table: Table,
        elementIndex: Int,
        value: Any?,
        elementsToFill: Int,
    )

    fun copyTable(
        source: Table,
        sourceElementIndex: Int,
        destination: Table,
        destinationElementIndex: Int,
        elementsToCopy: Int,
    )
}

internal class InterpreterTables(
    private val handles: EngineHandles,
    private val codec: ReferenceCodec,
    private val heap: WasmHeap,
) : TableAccess {

    override fun sizeTable(table: Table): Int {
        return handles.tablePayload(table).instance.elements.size
    }

    override fun growTable(
        table: Table,
        elementsToAdd: Int,
        initial: Any?,
    ): Int {

        val payload = handles.tablePayload(table)
        require(elementsToAdd >= 0) { "negative growth $elementsToAdd" }

        return withAccessorFailures {
            heap.withScope {
                payload.instance.grow(elementsToAdd, codec.encode(payload.referenceType, initial))
            }
        }
    }

    override fun readTable(
        table: Table,
        index: Int,
    ): Any? {

        val elements = handles.tablePayload(table).instance.elements
        checkIndex(index, elements.size)

        return codec.decode(elements[index])
    }

    override fun readTableElements(
        table: Table,
        buffer: Array<Any?>,
        elementIndex: Int,
        elementsToRead: Int,
        bufferIndex: Int,
    ): Array<Any?> {

        val elements = handles.tablePayload(table).instance.elements
        checkRange(elementIndex, elementsToRead, elements.size)
        checkRange(bufferIndex, elementsToRead, buffer.size)

        for (offset in 0 until elementsToRead) {
            buffer[bufferIndex + offset] = codec.decode(elements[elementIndex + offset])
        }

        return buffer
    }

    override fun writeTable(
        table: Table,
        index: Int,
        value: Any?,
    ) {

        val payload = handles.tablePayload(table)
        checkIndex(index, payload.instance.elements.size)

        withAccessorFailures {
            heap.withScope {
                payload.instance.elements[index] = codec.encode(payload.referenceType, value)
            }
        }
    }

    override fun writeTableElements(
        table: Table,
        elementIndex: Int,
        buffer: Array<Any?>,
        bufferIndex: Int,
        elementsToWrite: Int,
    ) {

        val payload = handles.tablePayload(table)
        checkRange(elementIndex, elementsToWrite, payload.instance.elements.size)
        checkRange(bufferIndex, elementsToWrite, buffer.size)

        withAccessorFailures {
            heap.withScope {
                val words = LongArray(elementsToWrite) { offset -> codec.encode(payload.referenceType, buffer[bufferIndex + offset]) }
                words.copyInto(payload.instance.elements, elementIndex)
            }
        }
    }

    override fun fillTable(
        table: Table,
        elementIndex: Int,
        value: Any?,
        elementsToFill: Int,
    ) {

        val payload = handles.tablePayload(table)
        checkRange(elementIndex, elementsToFill, payload.instance.elements.size)

        withAccessorFailures {
            heap.withScope {
                val raw = codec.encode(payload.referenceType, value)
                payload.instance.elements.fill(raw, elementIndex, elementIndex + elementsToFill)
            }
        }
    }

    override fun copyTable(
        source: Table,
        sourceElementIndex: Int,
        destination: Table,
        destinationElementIndex: Int,
        elementsToCopy: Int,
    ) {

        val sourcePayload = handles.tablePayload(source)
        val destinationPayload = handles.tablePayload(destination)
        val sourceElements = sourcePayload.instance.elements
        checkRange(sourceElementIndex, elementsToCopy, sourceElements.size)
        checkRange(destinationElementIndex, elementsToCopy, destinationPayload.instance.elements.size)

        val destinationType = destinationPayload.referenceType
        if (sourcePayload.referenceType.referenceType != destinationType.referenceType) {
            for (index in sourceElementIndex until sourceElementIndex + elementsToCopy) {
                require(codec.matches(destinationType, sourceElements[index])) {
                    "element $index is not a value of ${destinationType.referenceType}"
                }
            }
        }

        sourceElements.copyInto(destinationPayload.instance.elements, destinationElementIndex, sourceElementIndex, sourceElementIndex + elementsToCopy)
    }
}
