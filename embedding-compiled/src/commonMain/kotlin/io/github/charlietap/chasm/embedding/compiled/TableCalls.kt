package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.host.UnsafeHostApi

fun sizeTable(table: Table): Int {
    return table.engine.sizeTable(table)
}

fun growTable(
    table: Table,
    elementsToAdd: Int,
    initial: Any?,
): Int {
    return table.engine.growTable(table, elementsToAdd, initial)
}

fun readTable(
    table: Table,
    index: Int,
): Any? {
    return table.engine.readTable(table, index)
}

fun writeTable(
    table: Table,
    index: Int,
    value: Any?,
) {
    table.engine.writeTable(table, index, value)
}

fun readTable(
    table: Table,
    buffer: Array<Any?>,
    elementIndex: Int,
    elementsToRead: Int,
    bufferIndex: Int = 0,
): Array<Any?> {
    return table.engine.readTable(table, buffer, elementIndex, elementsToRead, bufferIndex)
}

fun writeTable(
    table: Table,
    elementIndex: Int,
    buffer: Array<Any?>,
    bufferIndex: Int = 0,
    elementsToWrite: Int = buffer.size - bufferIndex,
) {
    table.engine.writeTable(table, elementIndex, buffer, bufferIndex, elementsToWrite)
}

fun fillTable(
    table: Table,
    elementIndex: Int,
    value: Any?,
    elementsToFill: Int,
) {
    table.engine.fillTable(table, elementIndex, value, elementsToFill)
}

fun copyTable(
    source: Table,
    sourceElementIndex: Int,
    destination: Table,
    destinationElementIndex: Int,
    elementsToCopy: Int,
) {
    destination.engine.copyTable(source, sourceElementIndex, destination, destinationElementIndex, elementsToCopy)
}

fun moveTable(
    source: Table,
    sourceElementIndex: Int,
    destination: Table,
    destinationElementIndex: Int,
    elementsToMove: Int,
) {
    destination.engine.copyTable(source, sourceElementIndex, destination, destinationElementIndex, elementsToMove)
}

@UnsafeHostApi
fun unsafeWriteTable(
    table: Table,
    index: Int,
    value: Any?,
) {
    table.engine.unsafeWriteTable(table, index, value)
}

@UnsafeHostApi
fun unsafeBorrowElements(table: Table): Array<Any?> {
    return table.engine.unsafeBorrowElements(table)
}
