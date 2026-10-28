package io.github.charlietap.chasm.declarations

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.declarations.ext.clone
import io.github.charlietap.chasm.embedding.shapes.ExportDefinition
import io.github.charlietap.chasm.embedding.shapes.FunctionNameData
import io.github.charlietap.chasm.embedding.shapes.ImportDefinition
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ext.functionType

fun moduleInfo(
    module: ModuleDeclarations,
): ModuleInfo {
    val importedFunctions = ArrayList<Int>()
    val importedTables = ArrayList<TableType>()
    val importedMemories = ArrayList<MemoryType>()
    val importedGlobals = ArrayList<GlobalType>()
    val importedTags = ArrayList<TagType>()

    val imports = module.imports.map { import ->
        val (index, type) = when (val descriptor = import.descriptor) {
            is Import.Descriptor.Function -> {
                importedFunctions += descriptor.typeIndex.toInt()
                ModuleIndex.FunctionIndex(importedFunctions.lastIndex) to
                    ExternalType.Function(functionType(module, descriptor.typeIndex.toInt()))
            }
            is Import.Descriptor.Table -> {
                importedTables += descriptor.type
                ModuleIndex.TableIndex(importedTables.lastIndex) to ExternalType.Table(descriptor.type)
            }
            is Import.Descriptor.Memory -> {
                importedMemories += descriptor.type
                ModuleIndex.MemoryIndex(importedMemories.lastIndex) to ExternalType.Memory(descriptor.type)
            }
            is Import.Descriptor.Global -> {
                importedGlobals += descriptor.type
                ModuleIndex.GlobalIndex(importedGlobals.lastIndex) to ExternalType.Global(descriptor.type)
            }
            is Import.Descriptor.Tag -> {
                importedTags += descriptor.type
                ModuleIndex.TagIndex(importedTags.lastIndex) to ExternalType.Tag(descriptor.type)
            }
        }
        ImportDefinition(import.moduleName.name, import.entityName.name, index, type)
    }

    val exports = module.exports.map { export ->
        val name = export.name.name
        when (val descriptor = export.descriptor) {
            is Export.Descriptor.Function -> {
                val index = descriptor.functionIndex.toInt()
                val typeIndex = importedFunctions.getOrNull(index)
                    ?: module.functionTypeIndices.getOrNull(index - importedFunctions.size)
                val type = typeIndex?.let { module.definedTypes.getOrNull(it)?.functionType() }
                    ?: throw NoSuchElementException("No function found for exported index $index")
                ExportDefinition(
                    name = name,
                    index = ModuleIndex.FunctionIndex(index),
                    type = ExternalType.Function(type),
                    nameData = functionNameData(module, index),
                )
            }
            is Export.Descriptor.Table -> {
                val index = descriptor.tableIndex.toInt()
                ExportDefinition(
                    name = name,
                    index = ModuleIndex.TableIndex(index),
                    type = ExternalType.Table(
                        importedTables.getOrNull(index)
                            ?: module.tables.getOrNull(index - importedTables.size)?.type
                            ?: throw NoSuchElementException("No table found for exported index $index"),
                    ),
                )
            }
            is Export.Descriptor.Memory -> {
                val index = descriptor.memoryIndex.toInt()
                ExportDefinition(
                    name = name,
                    index = ModuleIndex.MemoryIndex(index),
                    type = ExternalType.Memory(
                        importedMemories.getOrNull(index)
                            ?: module.memories.getOrNull(index - importedMemories.size)
                            ?: throw NoSuchElementException("No memory found for exported index $index"),
                    ),
                )
            }
            is Export.Descriptor.Global -> {
                val index = descriptor.globalIndex.toInt()
                ExportDefinition(
                    name = name,
                    index = ModuleIndex.GlobalIndex(index),
                    type = ExternalType.Global(
                        importedGlobals.getOrNull(index)
                            ?: module.globals.getOrNull(index - importedGlobals.size)?.type
                            ?: throw NoSuchElementException("No global found for exported index $index"),
                    ),
                )
            }
            is Export.Descriptor.Tag -> {
                val index = descriptor.tagIndex.toInt()
                ExportDefinition(
                    name = name,
                    index = ModuleIndex.TagIndex(index),
                    type = ExternalType.Tag(
                        importedTags.getOrNull(index)
                            ?: module.tags.getOrNull(index - importedTags.size)
                            ?: throw NoSuchElementException("No tag found for exported index $index"),
                    ),
                )
            }
        }
    }

    return ModuleInfo(
        imports = imports.map { definition -> definition.copy(type = definition.type.clone()) },
        exports = exports.map { definition -> definition.copy(type = definition.type.clone()) },
    )
}

private fun functionType(
    module: ModuleDeclarations,
    typeIndex: Int,
): FunctionType =
    module.definedTypes[typeIndex].functionType()!!

private fun functionNameData(
    module: ModuleDeclarations,
    functionIndex: Int,
): FunctionNameData? {
    val name = module.names.functionName(functionIndex)
    val localNames = module.names.exportedLocalNames[functionIndex]?.toList()
    return if (name != null || localNames != null) FunctionNameData(name, localNames) else null
}
