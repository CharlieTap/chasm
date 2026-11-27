package io.github.charlietap.chasm.runtime.compiled.gc

internal class TableOperations(
    private val checks: ReferenceChecks,
) {

    fun sizeTable(table: ReferenceTable): Int {
        return table.elements.size
    }

    fun readTable(
        table: ReferenceTable,
        index: Int,
    ): Any? {

        val elements = table.elements

        checkIndex(index, elements.size)

        return elements[index]
    }

    fun readTable(
        table: ReferenceTable,
        buffer: Array<Any?>,
        elementIndex: Int,
        count: Int,
        bufferIndex: Int,
    ): Array<Any?> {

        val elements = table.elements

        checkRange(elementIndex, count, elements.size)
        checkRange(bufferIndex, count, buffer.size)

        System.arraycopy(elements, elementIndex, buffer, bufferIndex, count)

        return buffer
    }

    fun writeTable(
        table: ReferenceTable,
        index: Int,
        value: Any?,
    ) {

        checkIndex(index, table.elements.size)
        require(check(table).accepts(value)) { "the value does not match the table's element type" }

        table.elements[index] = value
    }

    fun writeTable(
        table: ReferenceTable,
        elementIndex: Int,
        buffer: Array<Any?>,
        bufferIndex: Int,
        count: Int,
    ) {

        val check = check(table)

        checkRange(elementIndex, count, table.elements.size)
        checkRange(bufferIndex, count, buffer.size)
        for (index in bufferIndex until bufferIndex + count) {
            require(check.accepts(buffer[index])) { "element $index does not match the table's element type" }
        }

        System.arraycopy(buffer, bufferIndex, table.elements, elementIndex, count)
    }

    fun unsafeWriteTable(
        table: ReferenceTable,
        index: Int,
        value: Any?,
    ) {
        table.elements[index] = value
    }

    fun growTable(
        table: ReferenceTable,
        count: Int,
        initial: Any?,
    ): Int {

        require(count >= 0) { "negative growth $count" }
        require(check(table).accepts(initial)) { "the value does not match the table's element type" }

        return tableGrowWithoutTrap(table, count, initial)
    }

    fun fillTable(
        table: ReferenceTable,
        index: Int,
        value: Any?,
        count: Int,
    ) {

        checkRange(index, count, table.elements.size)
        require(check(table).accepts(value)) { "the value does not match the table's element type" }

        table.elements.fill(value, index, index + count)
    }

    fun copyTable(
        source: ReferenceTable,
        sourceIndex: Int,
        destination: ReferenceTable,
        destinationIndex: Int,
        count: Int,
    ) {

        val sourceElements = source.elements

        checkRange(sourceIndex, count, sourceElements.size)
        checkRange(destinationIndex, count, destination.elements.size)

        if (source !== destination && source.instance.type.referenceType != destination.instance.type.referenceType) {
            val check = check(destination)
            for (index in sourceIndex until sourceIndex + count) {
                require(check.accepts(sourceElements[index])) { "element $index does not match the destination table" }
            }
        }

        System.arraycopy(sourceElements, sourceIndex, destination.elements, destinationIndex, count)
    }

    fun borrowElements(table: ReferenceTable): Array<Any?> {
        return table.elements
    }

    private fun check(table: ReferenceTable): ReferenceCheck {
        return table.check ?: checks.check(table.instance.type.referenceType, null).also { check -> table.check = check }
    }
}
