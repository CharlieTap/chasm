package io.github.charlietap.chasm.host

import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.global.readGlobalReference
import io.github.charlietap.chasm.embedding.global.writeGlobal
import io.github.charlietap.chasm.embedding.global.writeGlobalReference
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.table.copyTable
import io.github.charlietap.chasm.embedding.table.fillTable
import io.github.charlietap.chasm.embedding.table.growTable
import io.github.charlietap.chasm.embedding.table.moveTable
import io.github.charlietap.chasm.embedding.table.readTable
import io.github.charlietap.chasm.embedding.table.readTableElements
import io.github.charlietap.chasm.embedding.table.sizeTable
import io.github.charlietap.chasm.embedding.table.writeTable
import io.github.charlietap.chasm.embedding.table.writeTableElements

context(module: HostModuleInstance, resources: HostResources)
inline fun <T> withTable(
    index: ModuleIndex.TableIndex,
    block: context(ModuleIndex.TableIndex) Table.() -> T,
): T {
    return context(index) { resources.table(module, index).block() }
}

context(module: HostModuleInstance, resources: HostResources)
inline fun <T> withTable(
    index: Int,
    block: context(ModuleIndex.TableIndex) Table.() -> T,
): T {
    return withTable(ModuleIndex.TableIndex(index), block)
}

inline val Table.size: Int
    get() = sizeTable(this)

inline fun Table.read(index: Int): Any? {
    return readTable(this, index)
}

inline fun Table.write(
    index: Int,
    value: Any?,
) {
    writeTable(this, index, value)
}

inline fun Table.readElements(
    buffer: Array<Any?>,
    elementIndex: Int,
    elementsToRead: Int,
    bufferIndex: Int = 0,
): Array<Any?> {
    return readTableElements(this, buffer, elementIndex, elementsToRead, bufferIndex)
}

inline fun Table.writeElements(
    elementIndex: Int,
    buffer: Array<Any?>,
    bufferIndex: Int = 0,
    elementsToWrite: Int = buffer.size - bufferIndex,
) {
    writeTableElements(this, elementIndex, buffer, bufferIndex, elementsToWrite)
}

inline fun Table.fill(
    elementIndex: Int,
    value: Any?,
    elementsToFill: Int,
) {
    fillTable(this, elementIndex, value, elementsToFill)
}

inline fun Table.copy(
    sourceElementIndex: Int,
    destinationElementIndex: Int,
    elementsToCopy: Int,
    source: Table = this,
) {
    copyTable(source, sourceElementIndex, this, destinationElementIndex, elementsToCopy)
}

inline fun Table.move(
    sourceElementIndex: Int,
    destinationElementIndex: Int,
    elementsToMove: Int,
    source: Table = this,
) {
    moveTable(source, sourceElementIndex, this, destinationElementIndex, elementsToMove)
}

inline fun Table.grow(
    elementsToAdd: Int,
    initial: Any?,
): Int {
    return growTable(this, elementsToAdd, initial)
}

context(module: HostModuleInstance, resources: HostResources)
inline fun <T> withGlobal(
    index: ModuleIndex.GlobalIndex,
    block: context(ModuleIndex.GlobalIndex) Global.() -> T,
): T {
    return context(index) { resources.global(module, index).block() }
}

context(module: HostModuleInstance, resources: HostResources)
inline fun <T> withGlobal(
    index: Int,
    block: context(ModuleIndex.GlobalIndex) Global.() -> T,
): T {
    return withGlobal(ModuleIndex.GlobalIndex(index), block)
}

inline var Global.rawValue: Long
    get() = readGlobal(this)
    set(value) = writeGlobal(this, value)

inline var Global.referenceValue: Any?
    get() = readGlobalReference(this)
    set(value) = writeGlobalReference(this, value)
