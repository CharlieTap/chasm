package io.github.charlietap.chasm.runtime.host

import io.github.charlietap.chasm.host.ModuleIndex

interface RawHostResources {

    val references: HostReferences

    val gc: HostGc

    val externs: HostExterns

    val exceptions: HostExceptions

    fun memory(module: RawHostModuleInstance, index: ModuleIndex.MemoryIndex): HostMemory

    fun growMemory(
        module: RawHostModuleInstance,
        index: ModuleIndex.MemoryIndex,
        pagesToAdd: Int,
    ): Int

    fun table(module: RawHostModuleInstance, index: ModuleIndex.TableIndex): HostTable

    fun growTable(
        module: RawHostModuleInstance,
        index: ModuleIndex.TableIndex,
        elementsToAdd: Int,
        value: HostReference,
    ): Int

    fun global(module: RawHostModuleInstance, index: ModuleIndex.GlobalIndex): HostGlobal

    fun tag(module: RawHostModuleInstance, index: ModuleIndex.TagIndex): HostTag
}
