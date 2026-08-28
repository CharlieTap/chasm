package io.github.charlietap.chasm.host

import io.github.charlietap.chasm.embedding.memory.growMemoryInstance
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE

/** Grows this memory and returns its previous size in pages, or `-1` on failure. */
context(module: HostModuleInstance, resources: HostResources)
fun HostMemory.grow(pagesToAdd: Int): Int {
    if (pagesToAdd == 0) {
        return byteSize / PAGE_SIZE
    }
    if (pagesToAdd < 0) {
        return -1
    }

    val context = resources as ExecutionContext
    val instance = module as ModuleInstance
    val store = context.store
    val addresses = instance.memAddresses
    var index = 0
    var memory = store.memories[addresses[index].address]

    while (memory.data !== this) {
        index++
        memory = store.memories[addresses[index].address]
    }

    return growMemoryInstance(memory, pagesToAdd)
}

/** Grows this table and returns its previous size, or `-1` on failure. */
context(_: HostModuleInstance, _: HostResources)
fun HostTable.grow(elementsToAdd: Int, value: HostReference): Int =
    growTableInstance(this as TableInstance, elementsToAdd, value)

private fun growTableInstance(table: TableInstance, elementsToAdd: Int, value: HostReference): Int {
    val currentSize = table.elements.size

    if (elementsToAdd == 0) {
        return currentSize
    }

    val newSize = currentSize + elementsToAdd
    val maximumSize = table.type.limits.max?.toInt() ?: Int.MAX_VALUE

    if (elementsToAdd < 0 || newSize < currentSize || newSize > maximumSize) {
        return -1
    }

    val grown = table.elements.copyOf(newSize)
    grown.fill(value, currentSize, newSize)

    table.elements = grown
    table.type.limits.min = newSize.toULong()

    return currentSize
}
