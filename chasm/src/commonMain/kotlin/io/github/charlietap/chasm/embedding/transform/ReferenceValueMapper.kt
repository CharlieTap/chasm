package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.embedding.shapes.HeapType
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.ast.type.HeapType as InternalHeapType

internal class ReferenceValueMapper(
    private val heapTypeMapper: BidirectionalMapper<HeapType, InternalHeapType> = HeapTypeMapper,
) : BidirectionalMapper<Value.Reference, ReferenceValue> {

    override fun map(input: Value.Reference): ReferenceValue {
        return when (input) {
            is Value.Reference.Array -> input.value
            is Value.Reference.Exception -> input.value
            is Value.Reference.Extern -> ReferenceValue.Extern(map(input.value))
            is Value.Reference.Func -> ReferenceValue.Function(Address.Function(input.address))
            is Value.Reference.Host -> ReferenceValue.Host(input.value)
            is Value.Reference.I31 -> ReferenceValue.I31(input.value)
            is Value.Reference.Null -> ReferenceValue.Null(heapTypeMapper.map(input.heapType))
            is Value.Reference.Struct -> input.value
        }
    }

    override fun bimap(input: ReferenceValue): Value.Reference {
        return when (input) {
            is ReferenceValue.Array -> Value.Reference.Array(input)
            is ReferenceValue.Exception -> Value.Reference.Exception(input)
            is ReferenceValue.Extern -> Value.Reference.Extern(bimap(input.referenceValue))
            is ReferenceValue.Function -> Value.Reference.Func(input.address.address)
            is ReferenceValue.Host -> Value.Reference.Host(input.address)
            is ReferenceValue.I31 -> Value.Reference.I31(input.value)
            is ReferenceValue.Null -> Value.Reference.Null(heapTypeMapper.bimap(input.heapType))
            is ReferenceValue.Struct -> Value.Reference.Struct(input)
        }
    }

    companion object {
        val instance = ReferenceValueMapper()
    }
}
