package io.github.charlietap.chasm.host

import io.github.charlietap.chasm.runtime.instance.TableInstance

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
