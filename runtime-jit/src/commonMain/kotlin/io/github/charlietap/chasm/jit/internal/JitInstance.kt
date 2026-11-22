package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.shapes.Exports
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.PreparedFunction
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

internal class JitInstance(
    val module: ModuleInstance,
    val config: RuntimeConfig,
    private val handles: JitHandles,
) {

    @Volatile
    var prepared: Map<String, PreparedFunction> = emptyMap()

    @Volatile
    var exports: Exports? = null

    private val memories = arrayOfNulls<Memory>(module.memAddresses.size)
    private val tables = arrayOfNulls<Table>(module.tableAddresses.size)
    private val globals = arrayOfNulls<Global>(module.globalAddresses.size)
    private val tags = arrayOfNulls<Tag>(module.tagAddresses.size)

    fun memory(index: Int): Memory {
        return memories[index] ?: handles.memoryAt(module.memAddresses[index]).also { memory -> memories[index] = memory }
    }

    fun table(index: Int): Table {
        return tables[index] ?: handles.tableAt(module.tableAddresses[index]).also { table -> tables[index] = table }
    }

    fun global(index: Int): Global {
        return globals[index] ?: handles.globalAt(module.globalAddresses[index]).also { global -> globals[index] = global }
    }

    fun tag(index: Int): Tag {
        return tags[index] ?: handles.tagAt(module.tagAddresses[index]).also { tag -> tags[index] = tag }
    }
}
