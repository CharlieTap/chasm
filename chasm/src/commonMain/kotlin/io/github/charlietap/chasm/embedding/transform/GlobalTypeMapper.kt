package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.embedding.shapes.GlobalType
import io.github.charlietap.chasm.embedding.shapes.Mutability
import io.github.charlietap.chasm.embedding.shapes.ValueType
import io.github.charlietap.chasm.ast.type.GlobalType as InternalGlobalType
import io.github.charlietap.chasm.ast.type.Mutability as InternalMutability
import io.github.charlietap.chasm.ast.type.ValueType as InternalValueType

internal class GlobalTypeMapper(
    private val mutabilityMapper: BidirectionalMapper<Mutability, InternalMutability> = MutabilityMapper,
    private val valueTypeMapper: BidirectionalMapper<ValueType, InternalValueType> = ValueTypeMapper.instance,
) : BidirectionalMapper<GlobalType, InternalGlobalType> {
    override fun map(input: GlobalType): InternalGlobalType {
        return InternalGlobalType(
            valueTypeMapper.map(input.valueType),
            mutabilityMapper.map(input.mutability),
        )
    }

    override fun bimap(input: InternalGlobalType): GlobalType {
        return GlobalType(
            valueTypeMapper.bimap(input.valueType),
            mutabilityMapper.bimap(input.mutability),
        )
    }

    companion object {
        val instance = GlobalTypeMapper()
    }
}
