package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.chasm.type.ext.functionType

internal class ExportDescriptorMapper(
    private val module: Module,
) : Mapper<Export.Descriptor, ExternalType> {

    override fun map(input: Export.Descriptor): ExternalType {
        return when (input) {
            is Export.Descriptor.Function -> {
                val function = module.functions.first { function ->
                    function.idx == input.functionIndex
                }
                val functionType = module.types[function.typeIndex.idx.toInt()].recursiveType.functionType()
                ExternalType.Function(functionType!!)
            }
            is Export.Descriptor.Global -> {
                val global = module.globals.first { global ->
                    global.idx == input.globalIndex
                }
                ExternalType.Global(global.type)
            }
            is Export.Descriptor.Memory -> {
                val memory = module.memories.first { memory ->
                    memory.idx == input.memoryIndex
                }
                ExternalType.Memory(memory.type)
            }
            is Export.Descriptor.Table -> {
                val table = module.tables.first { table ->
                    table.idx == input.tableIndex
                }
                ExternalType.Table(table.type)
            }
            is Export.Descriptor.Tag -> {
                val tag = module.tags.first { tag ->
                    tag.index == input.tagIndex
                }
                ExternalType.Tag(tag.type)
            }
        }
    }
}
