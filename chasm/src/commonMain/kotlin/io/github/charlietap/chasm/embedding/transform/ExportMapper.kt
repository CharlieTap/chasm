package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.FunctionNameSubsection
import io.github.charlietap.chasm.ast.module.LocalNameSubsection
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.embedding.ext.nameSubsection
import io.github.charlietap.chasm.embedding.shapes.ExportDefinition
import io.github.charlietap.chasm.embedding.shapes.FunctionNameData
import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.chasm.type.ext.functionType

internal class ExportMapper(
    private val module: Module,
) : Mapper<Export, ExportDefinition> {

    override fun map(input: Export): ExportDefinition {
        return when (val descriptor = input.descriptor) {
            is Export.Descriptor.Function -> {
                val function = module.functions.first { function ->
                    function.idx == descriptor.functionIndex
                }
                val functionType = module.definedTypes[function.typeIndex.idx.toInt()].functionType()

                ExportDefinition(
                    name = input.name.name,
                    type = ExternalType.Function(functionType!!),
                    nameData = functionNameData(descriptor),
                )
            }
            is Export.Descriptor.Global -> {
                val global = module.globals.first { global ->
                    global.idx == descriptor.globalIndex
                }

                ExportDefinition(
                    name = input.name.name,
                    type = ExternalType.Global(global.type),
                )
            }
            is Export.Descriptor.Memory -> {
                val memory = module.memories.first { memory ->
                    memory.idx == descriptor.memoryIndex
                }

                ExportDefinition(
                    name = input.name.name,
                    type = ExternalType.Memory(memory.type),
                )
            }
            is Export.Descriptor.Table -> {
                val table = module.tables.first { table ->
                    table.idx == descriptor.tableIndex
                }

                ExportDefinition(
                    name = input.name.name,
                    type = ExternalType.Table(table.type),
                )
            }
            is Export.Descriptor.Tag -> {
                val tag = module.tags.first { tag ->
                    tag.index == descriptor.tagIndex
                }

                ExportDefinition(
                    name = input.name.name,
                    type = ExternalType.Tag(tag.type),
                )
            }
        }
    }

    private fun functionNameData(
        descriptor: Export.Descriptor.Function,
    ): FunctionNameData? {
        val functionNameSubsection = module.nameSubsection<FunctionNameSubsection>()
        val localNameSubsection = module.nameSubsection<LocalNameSubsection>()

        val functionName = functionNameSubsection?.nameMap?.firstOrNull {
            it.idx == descriptor.functionIndex.idx
        }?.name?.name

        val localNames = localNameSubsection?.indirectNameMap?.firstOrNull {
            it.idx == descriptor.functionIndex.idx
        }?.nameMap?.map { it.name.name }

        return if (functionName != null || localNames != null) {
            FunctionNameData(
                name = functionName,
                localNames = localNames,
            )
        } else {
            null
        }
    }
}
