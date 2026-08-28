package io.github.charlietap.chasm.host

interface HostResources {

    val references: HostReferences

    val gc: HostGc

    fun memory(module: HostModuleInstance, index: Int): HostMemory

    fun table(module: HostModuleInstance, index: Int): HostTable

    fun global(module: HostModuleInstance, index: Int): HostGlobal

    fun tag(module: HostModuleInstance, index: Int): HostTag
}
