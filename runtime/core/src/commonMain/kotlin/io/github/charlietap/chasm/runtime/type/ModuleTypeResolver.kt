package io.github.charlietap.chasm.runtime.type

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.expansion.LegacyBlockTypeExpander
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.type.rolling.substitution.GlobalTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.ReferenceTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.Substitution
import io.github.charlietap.chasm.type.rolling.substitution.TableTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TagTypeSubstitutor

class ModuleTypeResolver(
    private val module: Module,
) {

    private val substitution = Substitution.TypeIndexToDefinedType(module.definedTypes)

    fun definedType(index: Index.TypeIndex): DefinedType = module.definedTypes[index.toInt()]

    fun functionType(index: Index.TypeIndex): FunctionType = definedType(index).functionType()
        ?: error("type ${index.idx} is not a function type")

    fun blockType(type: BlockType): FunctionType = LegacyBlockTypeExpander(module.definedTypes, type)
        ?: error("block type is not a function type: $type")

    fun resolve(type: TableType): TableType = TableTypeSubstitutor(type, substitution)

    fun resolve(type: GlobalType): GlobalType = GlobalTypeSubstitutor(type, substitution)

    fun resolve(type: TagType): TagType = TagTypeSubstitutor(type, substitution)

    fun resolve(type: ReferenceType): ReferenceType = ReferenceTypeSubstitutor(type, substitution)
}
