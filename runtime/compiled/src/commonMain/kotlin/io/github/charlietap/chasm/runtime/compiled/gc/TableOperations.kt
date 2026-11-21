package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.runtime.instance.TableInstance

class TableOperations(
    private val storage: ReferenceStorage,
    private val checks: ReferenceChecks,
) {

    fun sizeTable(table: TableInstance): Int {
        return storage.referenceTable(table).elements.size
    }

    fun readTable(
        table: TableInstance,
        index: Int,
    ): Any? {

        val elements = storage.referenceTable(table).elements

        checkIndex(index, elements.size)

        return elements[index]
    }

    fun readTable(
        table: TableInstance,
        buffer: Array<Any?>,
        elementIndex: Int,
        count: Int,
        bufferIndex: Int,
    ): Array<Any?> {

        val elements = storage.referenceTable(table).elements

        checkRange(elementIndex, count, elements.size)
        checkRange(bufferIndex, count, buffer.size)

        System.arraycopy(elements, elementIndex, buffer, bufferIndex, count)

        return buffer
    }

    fun writeTable(
        table: TableInstance,
        index: Int,
        value: Any?,
    ) {

        val reference = storage.referenceTable(table)

        checkIndex(index, reference.elements.size)
        require(check(reference).accepts(value)) { "the value does not match the table's element type" }

        reference.elements[index] = value
    }

    fun writeTable(
        table: TableInstance,
        elementIndex: Int,
        buffer: Array<Any?>,
        bufferIndex: Int,
        count: Int,
    ) {

        val reference = storage.referenceTable(table)
        val check = check(reference)

        checkRange(elementIndex, count, reference.elements.size)
        checkRange(bufferIndex, count, buffer.size)
        for (index in bufferIndex until bufferIndex + count) {
            require(check.accepts(buffer[index])) { "element $index does not match the table's element type" }
        }

        System.arraycopy(buffer, bufferIndex, reference.elements, elementIndex, count)
    }

    fun unsafeWriteTable(
        table: TableInstance,
        index: Int,
        value: Any?,
    ) {
        storage.referenceTable(table).elements[index] = value
    }

    fun growTable(
        table: TableInstance,
        count: Int,
        initial: Any?,
    ): Int {

        val reference = storage.referenceTable(table)

        require(count >= 0) { "negative growth $count" }
        require(check(reference).accepts(initial)) { "the value does not match the table's element type" }

        return tableGrow(reference, count, initial)
    }

    fun fillTable(
        table: TableInstance,
        index: Int,
        value: Any?,
        count: Int,
    ) {

        val reference = storage.referenceTable(table)

        checkRange(index, count, reference.elements.size)
        require(check(reference).accepts(value)) { "the value does not match the table's element type" }

        reference.elements.fill(value, index, index + count)
    }

    fun copyTable(
        source: TableInstance,
        sourceIndex: Int,
        destination: TableInstance,
        destinationIndex: Int,
        count: Int,
    ) {

        val sourceReference = storage.referenceTable(source)
        val destinationReference = storage.referenceTable(destination)
        val sourceElements = sourceReference.elements

        checkRange(sourceIndex, count, sourceElements.size)
        checkRange(destinationIndex, count, destinationReference.elements.size)

        if (source !== destination && source.type.referenceType != destination.type.referenceType) {
            val check = check(destinationReference)
            for (index in sourceIndex until sourceIndex + count) {
                require(check.accepts(sourceElements[index])) { "element $index does not match the destination table" }
            }
        }

        System.arraycopy(sourceElements, sourceIndex, destinationReference.elements, destinationIndex, count)
    }

    fun borrowElements(table: TableInstance): Array<Any?> {
        return storage.referenceTable(table).elements
    }

    private fun check(reference: ReferenceTable): ReferenceCheck {
        return reference.check ?: checks.check(reference.instance.type.referenceType, null).also { check -> reference.check = check }
    }
}
