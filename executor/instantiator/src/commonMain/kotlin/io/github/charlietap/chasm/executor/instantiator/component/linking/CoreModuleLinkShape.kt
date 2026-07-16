package io.github.charlietap.chasm.executor.instantiator.component.linking

import io.github.charlietap.chasm.ir.module.Export
import io.github.charlietap.chasm.ir.module.Import
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.type.component.CoreEntityType
import io.github.charlietap.chasm.type.component.CoreImportName
import io.github.charlietap.chasm.type.component.CoreModuleType

internal data class CoreModuleLinkShape(
    val type: CoreModuleType?,
    val exports: Map<String, Export>,
)

internal fun CoreModuleLinkShape(module: Module): CoreModuleLinkShape? {
    val importTypes = linkedMapOf<CoreImportName, CoreEntityType>()
    val functionImports = mutableListOf<CoreEntityType.Function>()
    val tableImports = mutableListOf<CoreEntityType.Table>()
    val memoryImports = mutableListOf<CoreEntityType.Memory>()
    val globalImports = mutableListOf<CoreEntityType.Global>()
    val tagImports = mutableListOf<CoreEntityType.Tag>()
    module.imports.forEach { import ->
        val type = import.type(module)
        val name = CoreImportName(import.moduleName.name, import.entityName.name)
        if (importTypes.put(name, type) != null) return null
        when (type) {
            is CoreEntityType.Function -> functionImports += type
            is CoreEntityType.Table -> tableImports += type
            is CoreEntityType.Memory -> memoryImports += type
            is CoreEntityType.Global -> globalImports += type
            is CoreEntityType.Tag -> tagImports += type
            is CoreEntityType.Type,
            is CoreEntityType.Module,
            is CoreEntityType.Instance,
            -> error("core module imports cannot contain component-only entities")
        }
    }

    val functions = module.functions.associateBy { function -> function.idx.idx }
    val tables = module.tables.associateBy { table -> table.idx.idx }
    val memories = module.memories.associateBy { memory -> memory.idx.idx }
    val globals = module.globals.associateBy { global -> global.idx.idx }
    val tags = module.tags.associateBy { tag -> tag.index.idx }
    val exportTypes = linkedMapOf<String, CoreEntityType>()
    val exports = linkedMapOf<String, Export>()
    var typesAvailable = true
    module.exports.forEach { export ->
        val name = export.name.name
        val type = when (val descriptor = export.descriptor) {
            is Export.Descriptor.Function -> functionImports.getOrNull(descriptor.functionIndex.idx)
                ?: functions[descriptor.functionIndex.idx]?.let { function ->
                    CoreEntityType.Function(module.definedTypes[function.typeIndex.idx])
                }
            is Export.Descriptor.Table -> tableImports.getOrNull(descriptor.tableIndex.idx)
                ?: tables[descriptor.tableIndex.idx]?.let { table -> CoreEntityType.Table(table.type) }
            is Export.Descriptor.Memory -> memoryImports.getOrNull(descriptor.memoryIndex.idx)
                ?: memories[descriptor.memoryIndex.idx]?.let { memory -> CoreEntityType.Memory(memory.type) }
            is Export.Descriptor.Global -> globalImports.getOrNull(descriptor.globalIndex.idx)
                ?: globals[descriptor.globalIndex.idx]?.let { global -> CoreEntityType.Global(global.type) }
            is Export.Descriptor.Tag -> tagImports.getOrNull(descriptor.tagIndex.idx)
                ?: tags[descriptor.tagIndex.idx]?.let { tag -> CoreEntityType.Tag(tag.type) }
        }
        if (exports.put(name, export) != null) return null
        if (type == null) {
            typesAvailable = false
        } else {
            exportTypes[name] = type
        }
    }

    return CoreModuleLinkShape(
        type = if (typesAvailable) CoreModuleType(imports = importTypes, exports = exportTypes) else null,
        exports = exports,
    )
}

internal fun Export.index(): Int = when (val descriptor = descriptor) {
    is Export.Descriptor.Function -> descriptor.functionIndex.idx
    is Export.Descriptor.Table -> descriptor.tableIndex.idx
    is Export.Descriptor.Memory -> descriptor.memoryIndex.idx
    is Export.Descriptor.Global -> descriptor.globalIndex.idx
    is Export.Descriptor.Tag -> descriptor.tagIndex.idx
}

private fun Import.type(module: Module): CoreEntityType = when (val descriptor = descriptor) {
    is Import.Descriptor.Function -> CoreEntityType.Function(module.definedTypes[descriptor.typeIndex.idx])
    is Import.Descriptor.Table -> CoreEntityType.Table(descriptor.type)
    is Import.Descriptor.Memory -> CoreEntityType.Memory(descriptor.type)
    is Import.Descriptor.Global -> CoreEntityType.Global(descriptor.type)
    is Import.Descriptor.Tag -> CoreEntityType.Tag(descriptor.type)
}
