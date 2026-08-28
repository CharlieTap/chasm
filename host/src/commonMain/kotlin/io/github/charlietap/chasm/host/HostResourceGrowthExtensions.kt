@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.host

/** Grows the memory at [index] and returns its previous size in pages, or `-1` on failure. */
context(module: HostModuleInstance, resources: HostResources)
inline fun growMemory(
    index: ModuleIndex.MemoryIndex,
    pagesToAdd: Int,
): Int = resources.growMemory(module, index, pagesToAdd)

/** Grows the memory at [index] and returns its previous size in pages, or `-1` on failure. */
context(module: HostModuleInstance, resources: HostResources)
inline fun growMemory(
    index: Int,
    pagesToAdd: Int,
): Int = growMemory(ModuleIndex.MemoryIndex(index), pagesToAdd)

/** Grows this memory and returns its previous size in pages, or `-1` on failure. */
context(module: HostModuleInstance, resources: HostResources, index: ModuleIndex.MemoryIndex)
inline fun HostMemory.grow(pagesToAdd: Int): Int = resources.growMemory(module, index, pagesToAdd)

/** Grows the table at [index] and returns its previous size, or `-1` on failure. */
context(module: HostModuleInstance, resources: HostResources)
inline fun growTable(
    index: ModuleIndex.TableIndex,
    elementsToAdd: Int,
    value: HostReference,
): Int = resources.growTable(module, index, elementsToAdd, value)

/** Grows the table at [index] and returns its previous size, or `-1` on failure. */
context(module: HostModuleInstance, resources: HostResources)
inline fun growTable(
    index: Int,
    elementsToAdd: Int,
    value: HostReference,
): Int = growTable(ModuleIndex.TableIndex(index), elementsToAdd, value)

/** Grows this table and returns its previous size, or `-1` on failure. */
context(module: HostModuleInstance, resources: HostResources, index: ModuleIndex.TableIndex)
inline fun HostTable.grow(
    elementsToAdd: Int,
    value: HostReference,
): Int = resources.growTable(module, index, elementsToAdd, value)
