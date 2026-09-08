package io.github.charlietap.chasm.runtime.execution

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.host.HostExceptions
import io.github.charlietap.chasm.host.HostExterns
import io.github.charlietap.chasm.host.HostGc
import io.github.charlietap.chasm.host.HostGlobal
import io.github.charlietap.chasm.host.HostMemory
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.HostReference
import io.github.charlietap.chasm.host.HostReferences
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.host.HostTable
import io.github.charlietap.chasm.host.HostTag
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.MAX_PAGES
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

data class ExecutionContext(
    val vstack: ValueStack,
    val store: Store,
    val instance: ModuleInstance,
    val config: RuntimeConfig,
) : HostResources {
    val heap: WasmHeap = store.heap

    override val references: HostReferences
        get() = heap

    override val gc: HostGc
        get() = heap

    override val externs: HostExterns
        get() = heap

    override val exceptions: HostExceptions
        get() = heap

    override fun memory(module: HostModuleInstance, index: ModuleIndex.MemoryIndex): HostMemory {
        val address = (module as ModuleInstance).memAddresses[index.index]
        return store.memories[address.address].data
    }

    override fun growMemory(module: HostModuleInstance, index: ModuleIndex.MemoryIndex, pagesToAdd: Int): Int {
        val address = (module as ModuleInstance).memAddresses[index.index]
        return store.memories[address.address].grow(pagesToAdd)
    }

    override fun table(module: HostModuleInstance, index: ModuleIndex.TableIndex): HostTable {
        val address = (module as ModuleInstance).tableAddresses[index.index]
        return store.tables[address.address]
    }

    override fun growTable(
        module: HostModuleInstance,
        index: ModuleIndex.TableIndex,
        elementsToAdd: Int,
        value: HostReference,
    ): Int {
        val address = (module as ModuleInstance).tableAddresses[index.index]
        return store.tables[address.address].grow(elementsToAdd, value)
    }

    override fun global(module: HostModuleInstance, index: ModuleIndex.GlobalIndex): HostGlobal {
        val address = (module as ModuleInstance).globalAddresses[index.index]
        return store.globals[address.address]
    }

    override fun tag(module: HostModuleInstance, index: ModuleIndex.TagIndex): HostTag {
        val address = (module as ModuleInstance).tagAddresses[index.index]
        return HostTag(address.address)
    }
}

private fun MemoryInstance.grow(pagesToAdd: Int): Int {
    val currentSize = type.limits.min.toInt()

    if (pagesToAdd == 0) {
        return currentSize
    }

    val newSize = currentSize + pagesToAdd
    val maximumSize = type.limits.max?.toInt() ?: MAX_PAGES

    if (pagesToAdd < 0 || newSize < currentSize || newSize > maximumSize) {
        return -1
    }

    data = data.grow(pagesToAdd)
    type.limits.min = newSize.toULong()
    refresh()

    return currentSize
}

private fun TableInstance.grow(elementsToAdd: Int, value: HostReference): Int {
    val currentSize = elements.size

    if (elementsToAdd == 0) {
        return currentSize
    }

    val newSize = currentSize + elementsToAdd
    val maximumSize = type.limits.max?.toInt() ?: Int.MAX_VALUE

    if (elementsToAdd < 0 || newSize < currentSize || newSize > maximumSize) {
        return -1
    }

    val grown = elements.copyOf(newSize)
    grown.fill(value, currentSize, newSize)

    elements = grown
    type.limits.min = newSize.toULong()

    return currentSize
}
