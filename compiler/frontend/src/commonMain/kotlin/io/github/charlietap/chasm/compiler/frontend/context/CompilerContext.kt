package io.github.charlietap.chasm.compiler.frontend.context

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.declarations.ModuleTypeResolver
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType

internal class CompilerContext(
    val module: Module,
    val types: ModuleTypeResolver,
    val globalTypes: Array<GlobalType> = emptyArray(),
    val tableTypes: Array<TableType> = emptyArray(),
    val tagTypes: Array<TagType> = emptyArray(),
    val importedFunctionCount: Int = 0,
    private val functionTypeIndices: IntArray = IntArray(0),
) {
    val emptyBlockType: FunctionType = types.blockType(BlockType.Empty)

    fun functionTypeAt(
        typeIndex: Int,
    ): FunctionType = types.functionType(Index.TypeIndex(typeIndex.toUInt()))

    fun functionType(
        functionIndex: Int,
    ): FunctionType = functionTypeAt(functionTypeIndices[functionIndex])

    fun indexedBlockType(
        type: BlockType.SignedTypeIndex,
    ): FunctionType = types.blockType(type)
}

internal fun createCompilerContext(
    module: Module,
    types: ModuleTypeResolver,
): CompilerContext {
    val importedGlobals = module.imports.mapNotNull { (it.descriptor as? Import.Descriptor.Global)?.type }
    val importedTables = module.imports.mapNotNull { (it.descriptor as? Import.Descriptor.Table)?.type }
    val importedTags = module.imports.mapNotNull { (it.descriptor as? Import.Descriptor.Tag)?.type }
    return CompilerContext(
        module = module,
        types = types,
        globalTypes = (importedGlobals + module.globals.map { it.type }).map(types::resolve).toTypedArray(),
        tableTypes = (importedTables + module.tables.map { it.type }).map(types::resolve).toTypedArray(),
        tagTypes = (importedTags + module.tags.map { it.type }).map(types::resolve).toTypedArray(),
        importedFunctionCount = module.imports.count { it.descriptor is Import.Descriptor.Function },
        functionTypeIndices = ModuleTypeResolver.functionTypeIndices(
            imports = module.imports,
            definedFunctionTypeIndices = IntArray(module.functions.size) { index -> module.functions[index].typeIndex.toInt() },
        ),
    )
}
