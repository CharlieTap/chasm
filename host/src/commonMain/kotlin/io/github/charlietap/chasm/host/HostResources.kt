package io.github.charlietap.chasm.host

interface HostResources {

    val references: HostReferences

    val gc: HostGc

    val externs: HostExterns

    val exceptions: HostExceptions

    fun memory(module: HostModuleInstance, index: ModuleIndex.MemoryIndex): HostMemory

    fun table(module: HostModuleInstance, index: ModuleIndex.TableIndex): HostTable

    fun global(module: HostModuleInstance, index: ModuleIndex.GlobalIndex): HostGlobal

    fun tag(module: HostModuleInstance, index: ModuleIndex.TagIndex): HostTag
}
