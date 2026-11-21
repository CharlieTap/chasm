package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.embedding.compiled.Global
import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Memory
import io.github.charlietap.chasm.embedding.compiled.Table
import io.github.charlietap.chasm.embedding.compiled.Tag
import io.github.charlietap.chasm.embedding.compiled.host.HostModuleInstance
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.embedding.shapes.Instance as ClassicInstance

internal interface JitHandles {

    fun installed(
        module: ModuleInstance,
        embedding: ClassicInstance,
    ): Instance

    fun instanceOf(module: ModuleInstance): Instance

    fun contextOf(module: ModuleInstance): HostModuleInstance

    fun memoryAt(address: Address.Memory): Memory

    fun tableAt(address: Address.Table): Table

    fun globalAt(address: Address.Global): Global

    fun tagAt(address: Address.Tag): Tag
}

internal class JitModuleContext(
    override val instance: Instance,
) : HostModuleInstance
