package io.github.charlietap.chasm.embedding.compiled.host

import io.github.charlietap.chasm.embedding.compiled.Global
import io.github.charlietap.chasm.embedding.compiled.Table
import io.github.charlietap.chasm.embedding.compiled.copyTable
import io.github.charlietap.chasm.embedding.compiled.fillTable
import io.github.charlietap.chasm.embedding.compiled.growTable
import io.github.charlietap.chasm.embedding.compiled.moveTable
import io.github.charlietap.chasm.embedding.compiled.readGlobal
import io.github.charlietap.chasm.embedding.compiled.readGlobalReference
import io.github.charlietap.chasm.embedding.compiled.readTable
import io.github.charlietap.chasm.embedding.compiled.sizeTable
import io.github.charlietap.chasm.embedding.compiled.writeGlobal
import io.github.charlietap.chasm.embedding.compiled.writeGlobalReference
import io.github.charlietap.chasm.embedding.compiled.writeTable
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.embedding.compiled.unsafeBorrowElements as catalogueUnsafeBorrowElements

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

inline fun Table.read(
    buffer: Array<Any?>,
    elementIndex: Int,
    elementsToRead: Int,
    bufferIndex: Int = 0,
): Array<Any?> {
    return readTable(this, buffer, elementIndex, elementsToRead, bufferIndex)
}

inline fun Table.write(
    elementIndex: Int,
    buffer: Array<Any?>,
    bufferIndex: Int = 0,
    elementsToWrite: Int = buffer.size - bufferIndex,
) {
    writeTable(this, elementIndex, buffer, bufferIndex, elementsToWrite)
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

@UnsafeHostApi
inline fun Table.unsafeBorrowElements(): Array<Any?> {
    return catalogueUnsafeBorrowElements(this)
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
