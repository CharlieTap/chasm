package io.github.charlietap.chasm.host

import io.github.charlietap.chasm.embedding.memory.growMemoryInstance
import io.github.charlietap.chasm.memory.grow.LinearMemoryGrower
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
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

    return growMemoryInstance(memory, pagesToAdd, ::LinearMemoryGrower)
}
