package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.embedding.shapes.ValueType

internal object ReferenceTypeMapper : BidirectionalMapper<ValueType.Reference, ReferenceType> {

    override fun map(input: ValueType.Reference): ReferenceType {
        return when (input) {
            is ValueType.Reference.Ref -> ReferenceType.Ref(input.heapType)
            is ValueType.Reference.RefNull -> ReferenceType.RefNull(input.heapType)
        }
    }

    override fun bimap(input: ReferenceType): ValueType.Reference {
        return when (input) {
            is ReferenceType.Ref -> ValueType.Reference.Ref(input.heapType)
            is ReferenceType.RefNull -> ValueType.Reference.RefNull(input.heapType)
        }
    }
}
