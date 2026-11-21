package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.embedding.compiled.Global
import io.github.charlietap.chasm.embedding.compiled.Memory
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.Table
import io.github.charlietap.chasm.embedding.compiled.Tag
import io.github.charlietap.chasm.embedding.compiled.host.HostModuleInstance
import io.github.charlietap.chasm.embedding.compiled.host.HostResources
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
        return module.instance.payload as JitInstance
    }
}
