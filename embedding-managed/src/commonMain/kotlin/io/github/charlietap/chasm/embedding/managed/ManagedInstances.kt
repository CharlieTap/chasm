package io.github.charlietap.chasm.embedding.managed

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

@InternalChasmApi
interface ManagedInstances {

    fun installed(
        module: ModuleInstance,
        config: RuntimeConfig,
    ): Instance

    fun instanceOf(module: ModuleInstance): Instance

    fun contextOf(module: ModuleInstance): HostModuleInstance

    fun memoryAt(address: Address.Memory): Memory

    fun tableAt(address: Address.Table): Table

    fun globalAt(address: Address.Global): Global

    fun tagAt(address: Address.Tag): Tag
}

internal class ManagedModuleContext(
    override val instance: Instance,
) : HostModuleInstance
