package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.chasm.embedding.shapes.Exports
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.PreparedFunction
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

internal class InterpreterInstance(
    val module: ModuleInstance,
    val config: RuntimeConfig,
    val handles: EngineHandles,
) {

    var exports: Exports? = null

    val prepared = HashMap<String, PreparedFunction>()

    private val memories = arrayOfNulls<Memory>(module.memAddresses.size)
    private val tables = arrayOfNulls<Table>(module.tableAddresses.size)
    private val globals = arrayOfNulls<Global>(module.globalAddresses.size)
    private val tags = arrayOfNulls<Tag>(module.tagAddresses.size)

    fun memory(index: Int): Memory {
        return memories[index] ?: handles.memory(module.memAddresses[index]).also { memory -> memories[index] = memory }
    }

    fun table(index: Int): Table {
        return tables[index] ?: handles.table(module.tableAddresses[index]).also { table -> tables[index] = table }
    }

    fun global(index: Int): Global {
        return globals[index] ?: handles.global(module.globalAddresses[index]).also { global -> globals[index] = global }
    }

    fun tag(index: Int): Tag {
        return tags[index] ?: handles.tag(module.tagAddresses[index]).also { tag -> tags[index] = tag }
    }
}

internal data class InterpreterModuleContext(
    override val instance: Instance,
    val interpreterInstance: InterpreterInstance,
) : HostModuleInstance

internal class PreparedCall(
    val owner: Engine,
    val instance: InterpreterInstance,
    val function: FunctionInstance,
    val parameterSlots: ReferenceSlots,
    val resultSlots: ReferenceSlots,
) {

    val usesReferenceLane: Boolean = !parameterSlots.isEmpty || !resultSlots.isEmpty
}
