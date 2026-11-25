package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.host.RawHostModuleInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

internal class InterpreterHostResources(
    private val binding: EngineBinding,
    private val handles: EngineHandles,
) : HostResources {

    override val store: Store
        get() = binding.store

    fun module(
        module: RawHostModuleInstance,
        context: ExecutionContext,
    ): HostModuleInstance {

        val instance = module as ModuleInstance

        return instance.compiledInstance as? InterpreterModuleContext ?: handles.context(instance, context.config)
    }

    override fun memory(
        module: HostModuleInstance,
        index: ModuleIndex.MemoryIndex,
    ): Memory {
        return interpreterInstance(module).memory(index.index)
    }

    override fun table(
        module: HostModuleInstance,
        index: ModuleIndex.TableIndex,
    ): Table {
        return interpreterInstance(module).table(index.index)
    }

    override fun global(
        module: HostModuleInstance,
        index: ModuleIndex.GlobalIndex,
    ): Global {
        return interpreterInstance(module).global(index.index)
    }

    override fun tag(
        module: HostModuleInstance,
        index: ModuleIndex.TagIndex,
    ): Tag {
        return interpreterInstance(module).tag(index.index)
    }

    private fun interpreterInstance(module: HostModuleInstance): InterpreterInstance {

        require(module is InterpreterModuleContext && module.interpreterInstance.handles === handles) {
            "the module instance belongs to a different store"
        }

        return module.interpreterInstance
    }
}
