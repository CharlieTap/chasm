package io.github.charlietap.chasm.declarations

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.expansion.BlockTypeExpander
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.type.rolling.substitution.GlobalTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.ReferenceTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.Substitution
import io.github.charlietap.chasm.type.rolling.substitution.TableTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TagTypeSubstitutor

class ModuleTypeResolver(
    private val definedTypes: List<DefinedType>,
    private val functionTypeIndices: IntArray,
) {

    constructor(
        declarations: ModuleDeclarations,
    ) : this(
        definedTypes = declarations.definedTypes,
        functionTypeIndices = functionTypeIndices(declarations.imports, declarations.functionTypeIndices),
    )

    private val substitution = Substitution.TypeIndexToDefinedType(definedTypes)

    fun definedType(index: Index.TypeIndex): DefinedType = definedTypes[index.toInt()]

    fun functionType(index: Index.TypeIndex): FunctionType = definedType(index).functionType()
        ?: error("type ${index.idx} is not a function type")

    fun functionType(index: Index.FunctionIndex): DefinedType = definedTypes[functionTypeIndices[index.toInt()]]

    fun blockType(type: BlockType): FunctionType = BlockTypeExpander(definedTypes, type)
        ?: error("block type is not a function type: $type")

    fun resolve(type: TableType): TableType = TableTypeSubstitutor(type.copy(), substitution)

    fun resolve(type: GlobalType): GlobalType = GlobalTypeSubstitutor(type.copy(), substitution)

    fun resolve(type: TagType): TagType = TagTypeSubstitutor(type.copy(functionType = type.functionType.copy()), substitution)

    fun resolve(type: ReferenceType): ReferenceType = ReferenceTypeSubstitutor(type, substitution)

    companion object {

        fun functionTypeIndices(
            imports: List<Import>,
            definedFunctionTypeIndices: IntArray,
        ): IntArray {
            val imported = imports.count { import -> import.descriptor is Import.Descriptor.Function }
            val indices = IntArray(imported + definedFunctionTypeIndices.size)
            var position = 0
            imports.forEach { import ->
                val descriptor = import.descriptor
                if (descriptor is Import.Descriptor.Function) indices[position++] = descriptor.typeIndex.idx.toInt()
            }
            definedFunctionTypeIndices.copyInto(indices, position)
            return indices
        }
    }
}
