package io.github.charlietap.chasm.embedding.managed

import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.host.ModuleIndex

internal class ManagedHostResources(
    override val store: Store,
) : HostResources {

    override fun memory(
        module: HostModuleInstance,
        index: ModuleIndex.MemoryIndex,
    ): Memory {
        return managed(module).memory(index.index)
    }

    override fun table(
        module: HostModuleInstance,
        index: ModuleIndex.TableIndex,
    ): Table {
        return managed(module).table(index.index)
    }

    override fun global(
        module: HostModuleInstance,
        index: ModuleIndex.GlobalIndex,
    ): Global {
        return managed(module).global(index.index)
    }

    override fun tag(
        module: HostModuleInstance,
        index: ModuleIndex.TagIndex,
    ): Tag {
        return managed(module).tag(index.index)
    }

    private fun managed(module: HostModuleInstance): ManagedInstance {
        return module.instance._payload() as ManagedInstance
    }
}
