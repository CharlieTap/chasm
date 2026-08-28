@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.host

/** Resolves [index] against the module in the current host-function context. */
context(module: HostModuleInstance)
inline fun HostResources.memory(index: ModuleIndex.MemoryIndex): HostMemory = memory(module, index)

/** Resolves [index] against the module in the current host-function context. */
context(module: HostModuleInstance)
inline fun HostResources.memory(index: Int): HostMemory = memory(ModuleIndex.MemoryIndex(index))

/** Resolves [index] against the module in the current host-function context. */
context(module: HostModuleInstance)
inline fun HostResources.table(index: ModuleIndex.TableIndex): HostTable = table(module, index)

/** Resolves [index] against the module in the current host-function context. */
context(module: HostModuleInstance)
inline fun HostResources.table(index: Int): HostTable = table(ModuleIndex.TableIndex(index))

/** Resolves [index] against the module in the current host-function context. */
context(module: HostModuleInstance)
inline fun HostResources.global(index: ModuleIndex.GlobalIndex): HostGlobal = global(module, index)

/** Resolves [index] against the module in the current host-function context. */
context(module: HostModuleInstance)
inline fun HostResources.global(index: Int): HostGlobal = global(ModuleIndex.GlobalIndex(index))

/** Resolves [index] against the module in the current host-function context. */
context(module: HostModuleInstance)
inline fun HostResources.tag(index: ModuleIndex.TagIndex): HostTag = tag(module, index)

/** Resolves [index] against the module in the current host-function context. */
context(module: HostModuleInstance)
inline fun HostResources.tag(index: Int): HostTag = tag(ModuleIndex.TagIndex(index))
