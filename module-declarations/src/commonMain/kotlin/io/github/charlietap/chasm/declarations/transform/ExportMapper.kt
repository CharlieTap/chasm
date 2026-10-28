package io.github.charlietap.chasm.declarations.transform

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.declarations.ext.clone
import io.github.charlietap.chasm.embedding.shapes.ExportDefinition
import io.github.charlietap.chasm.embedding.shapes.FunctionNameData
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ext.functionType

internal typealias ExportMapper = (ModuleDeclarations, Export) -> ExportDefinition

internal fun ExportMapper(
    module: ModuleDeclarations,
    input: Export,
): ExportDefinition {
    return when (val descriptor = input.descriptor) {
        is Export.Descriptor.Function -> {
            ExportDefinition(
                name = input.name.name,
                index = ModuleIndex.FunctionIndex(descriptor.functionIndex.toInt()),
                type = ExternalType.Function(exportedFunctionType(module, descriptor.functionIndex)).clone(),
                nameData = functionNameData(module, descriptor.functionIndex),
            )
        }
        is Export.Descriptor.Global -> {
            ExportDefinition(
                name = input.name.name,
                index = ModuleIndex.GlobalIndex(descriptor.globalIndex.toInt()),
                type = ExternalType.Global(exportedGlobalType(module, descriptor.globalIndex)).clone(),
            )
        }
        is Export.Descriptor.Memory -> {
            ExportDefinition(
                name = input.name.name,
                index = ModuleIndex.MemoryIndex(descriptor.memoryIndex.toInt()),
                type = ExternalType.Memory(exportedMemoryType(module, descriptor.memoryIndex)).clone(),
            )
        }
        is Export.Descriptor.Table -> {
            ExportDefinition(
                name = input.name.name,
                index = ModuleIndex.TableIndex(descriptor.tableIndex.toInt()),
                type = ExternalType.Table(exportedTableType(module, descriptor.tableIndex)).clone(),
            )
        }
        is Export.Descriptor.Tag -> {
            ExportDefinition(
                name = input.name.name,
                index = ModuleIndex.TagIndex(descriptor.tagIndex.toInt()),
                type = ExternalType.Tag(exportedTagType(module, descriptor.tagIndex)).clone(),
            )
        }
    }
}

private fun exportedFunctionType(
    module: ModuleDeclarations,
    index: Index.FunctionIndex,
): FunctionType {
    val typeIndex = module.resolve<Import.Descriptor.Function, Int>(
        position = index.toInt(),
        imported = { descriptor -> descriptor.typeIndex.toInt() },
        defined = { position -> module.functionTypeIndices.getOrNull(position) },
    )
    return typeIndex?.let { module.definedTypes.getOrNull(it)?.functionType() }
        ?: throw NoSuchElementException("No function found for exported index ${index.idx}")
}

private fun exportedGlobalType(
    module: ModuleDeclarations,
    index: Index.GlobalIndex,
): GlobalType = module.resolve<Import.Descriptor.Global, GlobalType>(
    position = index.toInt(),
    imported = { descriptor -> descriptor.type },
    defined = { position -> module.globals.getOrNull(position)?.type },
) ?: throw NoSuchElementException("No global found for exported index ${index.idx}")

private fun exportedMemoryType(
    module: ModuleDeclarations,
    index: Index.MemoryIndex,
): MemoryType = module.resolve<Import.Descriptor.Memory, MemoryType>(
    position = index.toInt(),
    imported = { descriptor -> descriptor.type },
    defined = { position -> module.memories.getOrNull(position) },
) ?: throw NoSuchElementException("No memory found for exported index ${index.idx}")

private fun exportedTableType(
    module: ModuleDeclarations,
    index: Index.TableIndex,
): TableType = module.resolve<Import.Descriptor.Table, TableType>(
    position = index.toInt(),
    imported = { descriptor -> descriptor.type },
    defined = { position -> module.tables.getOrNull(position)?.type },
) ?: throw NoSuchElementException("No table found for exported index ${index.idx}")

private fun exportedTagType(
    module: ModuleDeclarations,
    index: Index.TagIndex,
): TagType = module.resolve<Import.Descriptor.Tag, TagType>(
    position = index.toInt(),
    imported = { descriptor -> descriptor.type },
    defined = { position -> module.tags.getOrNull(position) },
) ?: throw NoSuchElementException("No tag found for exported index ${index.idx}")

private inline fun <reified T : Import.Descriptor, R> ModuleDeclarations.resolve(
    position: Int,
    imported: (T) -> R,
    defined: (Int) -> R?,
): R? {
    var importedCount = 0
    for (import in imports) {
        val descriptor = import.descriptor as? T ?: continue
        if (importedCount == position) return imported(descriptor)
        importedCount++
    }
    return defined(position - importedCount)
}

private fun functionNameData(
    module: ModuleDeclarations,
    index: Index.FunctionIndex,
): FunctionNameData? {
    val functionIndex = index.toInt()
    val functionName = module.names.functionName(functionIndex)
    val localNames = module.names.exportedLocalNames[functionIndex]?.toList()

    return if (functionName != null || localNames != null) {
        FunctionNameData(
            name = functionName,
            localNames = localNames,
        )
    } else {
        null
    }
}
