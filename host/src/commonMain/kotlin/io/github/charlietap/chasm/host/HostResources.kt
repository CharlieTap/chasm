package io.github.charlietap.chasm.host

interface HostResources {

    val references: HostReferences

    val gc: HostGc

    val externs: HostExterns

    val exceptions: HostExceptions

    fun memory(module: HostModuleInstance, index: ModuleIndex.MemoryIndex): HostMemory

    fun growMemory(
        module: HostModuleInstance,
        index: ModuleIndex.MemoryIndex,
        pagesToAdd: Int,
    ): Int

    fun table(module: HostModuleInstance, index: ModuleIndex.TableIndex): HostTable

    fun growTable(
        module: HostModuleInstance,
        index: ModuleIndex.TableIndex,
        elementsToAdd: Int,
        value: HostReference,
    ): Int

    fun global(module: HostModuleInstance, index: ModuleIndex.GlobalIndex): HostGlobal

    fun tag(module: HostModuleInstance, index: ModuleIndex.TagIndex): HostTag
}
