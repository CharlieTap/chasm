package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.FunctionNameSubsection
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
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
                val functionType = exportedFunctionType(descriptor.functionIndex)

                ExportDefinition(
                    name = input.name.name,
                    type = ExternalType.Function(functionType),
                    nameData = functionNameData(descriptor),
                )
            }
            is Export.Descriptor.Global -> {
                ExportDefinition(
                    name = input.name.name,
                    type = ExternalType.Global(exportedGlobalType(descriptor.globalIndex)),
                )
            }
            is Export.Descriptor.Memory -> {
                ExportDefinition(
                    name = input.name.name,
                    type = ExternalType.Memory(exportedMemoryType(descriptor.memoryIndex)),
                )
            }
            is Export.Descriptor.Table -> {
                ExportDefinition(
                    name = input.name.name,
                    type = ExternalType.Table(exportedTableType(descriptor.tableIndex)),
                )
            }
            is Export.Descriptor.Tag -> {
                ExportDefinition(
                    name = input.name.name,
                    type = ExternalType.Tag(exportedTagType(descriptor.tagIndex)),
                )
            }
        }
    }

    private fun exportedFunctionType(
        index: Index.FunctionIndex,
    ) = module.functions.firstOrNull { function ->
        function.idx == index
    }?.let { function ->
        module.definedTypes[function.typeIndex.idx.toInt()].functionType()
    } ?: module.imports
        .filterByDescriptor<Import.Descriptor.Function>()
        .getOrNull(index.idx.toInt())
        ?.let { descriptor ->
            module.definedTypes[descriptor.typeIndex.idx.toInt()].functionType()
        }
        ?: throw NoSuchElementException("No function found for exported index ${index.idx}")

    private fun exportedGlobalType(
        index: Index.GlobalIndex,
    ) = module.globals.firstOrNull { global ->
        global.idx == index
    }?.type ?: module.imports
        .filterByDescriptor<Import.Descriptor.Global>()
        .getOrNull(index.idx.toInt())
        ?.type
        ?: throw NoSuchElementException("No global found for exported index ${index.idx}")

    private fun exportedMemoryType(
        index: Index.MemoryIndex,
    ) = module.memories.firstOrNull { memory ->
        memory.idx == index
    }?.type ?: module.imports
        .filterByDescriptor<Import.Descriptor.Memory>()
        .getOrNull(index.idx.toInt())
        ?.type
        ?: throw NoSuchElementException("No memory found for exported index ${index.idx}")

    private fun exportedTableType(
        index: Index.TableIndex,
    ) = module.tables.firstOrNull { table ->
        table.idx == index
    }?.type ?: module.imports
        .filterByDescriptor<Import.Descriptor.Table>()
        .getOrNull(index.idx.toInt())
        ?.type
        ?: throw NoSuchElementException("No table found for exported index ${index.idx}")

    private fun exportedTagType(
        index: Index.TagIndex,
    ) = module.tags.firstOrNull { tag ->
        tag.index == index
    }?.type ?: module.imports
        .filterByDescriptor<Import.Descriptor.Tag>()
        .getOrNull(index.idx.toInt())
        ?.type
        ?: throw NoSuchElementException("No tag found for exported index ${index.idx}")

    private inline fun <reified T : Import.Descriptor> List<Import>.filterByDescriptor(): List<T> {
        return mapNotNull { import -> import.descriptor as? T }
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
