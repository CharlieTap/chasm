package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType

internal class ImmutableModuleValidationContext(
    val config: ModuleConfig,
    val module: Module,
    val types: List<DefinedType>,
    val functions: List<DefinedType>,
    val globals: List<GlobalType>,
    val memories: List<MemoryType>,
    val tables: List<TableType>,
    val tags: List<TagType>,
    val datas: List<Index.DataIndex>,
    val elems: List<ReferenceType>,
    val importedGlobalCount: Int,
    val refs: Set<Index.FunctionIndex>,
)

internal fun ImmutableModuleValidationContext(
    config: ModuleConfig,
    module: Module,
): ImmutableModuleValidationContext {
    val types = module.definedTypes
    val functions = mutableListOf<DefinedType>()
    val globals = mutableListOf<GlobalType>()
    val memories = mutableListOf<MemoryType>()
    val tables = mutableListOf<TableType>()
    val tags = mutableListOf<TagType>()

    module.imports.forEach { import ->
        when (val descriptor = import.descriptor) {
            is Import.Descriptor.Function -> types.getOrNull(descriptor.typeIndex.idx.toInt())?.let(functions::add)
            is Import.Descriptor.Global -> globals += descriptor.type
            is Import.Descriptor.Memory -> memories += descriptor.type
            is Import.Descriptor.Table -> tables += descriptor.type
            is Import.Descriptor.Tag -> tags += descriptor.type
        }
    }
    val importedGlobalCount = globals.size

    module.functions.mapNotNullTo(functions) { function ->
        types.getOrNull(function.typeIndex.idx.toInt())
    }
    module.globals.mapTo(globals, Global::type)
    module.memories.mapTo(memories, Memory::type)
    module.tables.mapTo(tables, Table::type)
    module.tags.mapTo(tags, Tag::type)

    return ImmutableModuleValidationContext(
        config = config,
        module = module,
        types = types,
        functions = functions,
        globals = globals,
        memories = memories,
        tables = tables,
        tags = tags,
        datas = module.dataSegments.map(DataSegment::idx),
        elems = module.elementSegments.map(ElementSegment::type),
        importedGlobalCount = importedGlobalCount,
        refs = collectDeclaredFunctionReferences(module),
    )
}
