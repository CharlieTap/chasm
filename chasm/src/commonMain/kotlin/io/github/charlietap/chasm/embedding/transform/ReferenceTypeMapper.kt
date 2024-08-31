package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.embedding.shapes.HeapType
import io.github.charlietap.chasm.embedding.shapes.ValueType
import io.github.charlietap.chasm.ast.type.HeapType as InternalHeapType

internal class ReferenceTypeMapper(
    private val heapTypeMapper: BidirectionalMapper<HeapType, InternalHeapType> = HeapTypeMapper,
) : BidirectionalMapper<ValueType.Reference, ReferenceType> {

    override fun map(input: ValueType.Reference): ReferenceType {
        return when (input) {
            is ValueType.Reference.Ref -> ReferenceType.Ref(heapTypeMapper.map(input.heapType))
            is ValueType.Reference.RefNull -> ReferenceType.RefNull(heapTypeMapper.map(input.heapType))
        }
    }

    override fun bimap(input: ReferenceType): ValueType.Reference {
        return when (input) {
            is ReferenceType.Ref -> ValueType.Reference.Ref(heapTypeMapper.bimap(input.heapType))
            is ReferenceType.RefNull -> ValueType.Reference.RefNull(heapTypeMapper.bimap(input.heapType))
        }
    }

    companion object {
        val instance = ReferenceTypeMapper()
    }
}
