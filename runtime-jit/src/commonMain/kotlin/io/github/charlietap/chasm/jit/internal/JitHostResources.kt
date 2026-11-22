package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.host.ModuleIndex

internal class JitHostResources(
    override val store: Store,
) : HostResources {

    override fun memory(
        module: HostModuleInstance,
        index: ModuleIndex.MemoryIndex,
    ): Memory {
        return jit(module).memory(index.index)
    }

    override fun table(
        module: HostModuleInstance,
        index: ModuleIndex.TableIndex,
    ): Table {
        return jit(module).table(index.index)
    }

    override fun global(
        module: HostModuleInstance,
        index: ModuleIndex.GlobalIndex,
    ): Global {
        return jit(module).global(index.index)
    }

    override fun tag(
        module: HostModuleInstance,
        index: ModuleIndex.TagIndex,
    ): Tag {
        return jit(module).tag(index.index)
    }

    private fun jit(module: HostModuleInstance): JitInstance {
        return module.instance._payload() as JitInstance
    }
}
