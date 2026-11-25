package io.github.charlietap.chasm.embedding.compiled.internal

import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType

internal class LaneValues {

    fun write(
        types: List<ValueType>,
        values: List<ExecutionValue>,
        words: LongArray,
        references: Array<Any?>,
    ): String? {

        if (values.size != types.size) {
            return "expected ${types.size} arguments but received ${values.size}"
        }

        types.forEachIndexed { index, type ->
            val value = values[index]
            when {
                type is ValueType.Number -> {
                    val word = word(type.numberType, value) ?: return "argument $index is not a ${type.numberType}"
                    words[index] = word
                }
                type is ValueType.Reference && value is ReferenceValue -> {
                    references[index] = reference(value)
                }
                type is ValueType.Reference -> {
                    return "argument $index is not a reference"
                }
                else -> {
                    return "argument $index has an unsupported type $type"
                }
            }
        }

        return null
    }

    fun read(
        types: List<ValueType>,
        words: LongArray,
        references: Array<Any?>,
    ): List<ExecutionValue> {
        return types.mapIndexed { index, type ->
            when (type) {
                is ValueType.Number -> number(type.numberType, words[index])
                is ValueType.Reference -> referenceValue(type, references[index])
                else -> throw IllegalStateException("result $index has an unsupported type $type")
            }
        }
    }

    fun number(
        type: ValueType,
        value: ExecutionValue,
    ): Long {

        val numberType = requireNotNull((type as? ValueType.Number)?.numberType) { "$type is not a number type" }

        return requireNotNull(word(numberType, value)) { "$value is not a $numberType" }
    }

    fun reference(value: ExecutionValue): Any? {
        return when (value) {
            is ReferenceValue.Null -> null
            is ReferenceValue.Struct -> value.reference
            is ReferenceValue.Array -> value.reference
            is ReferenceValue.I31 -> value.reference
            is ReferenceValue.Function -> value.reference
            is ReferenceValue.Exception -> value.reference
            is ReferenceValue.Extern -> value.value
            else -> throw IllegalArgumentException("$value is not a reference")
        }
    }

    private fun word(
        type: NumberType,
        value: ExecutionValue,
    ): Long? {
        return when {
            type == NumberType.I32 && value is NumberValue.I32 -> value.value.toLong()
            type == NumberType.I64 && value is NumberValue.I64 -> value.value
            type == NumberType.F32 && value is NumberValue.F32 -> value.value.toRawBits().toLong()
            type == NumberType.F64 && value is NumberValue.F64 -> value.value.toRawBits()
            else -> null
        }
    }

    private fun number(
        type: NumberType,
        word: Long,
    ): ExecutionValue {
        return when (type) {
            NumberType.I32 -> NumberValue.I32(word.toInt())
            NumberType.I64 -> NumberValue.I64(word)
            NumberType.F32 -> NumberValue.F32(Float.fromBits(word.toInt()))
            NumberType.F64 -> NumberValue.F64(Double.fromBits(word))
        }
    }

    private fun referenceValue(
        type: ValueType.Reference,
        value: Any?,
    ): ReferenceValue {

        val heap = type.referenceType.heapType

        return when {
            value == null -> ReferenceValue.Null(bottom(heap))
            heap == AbstractHeapType.Extern || heap == AbstractHeapType.NoExtern -> ReferenceValue.Extern(value)
            else -> objectValue(value)
        }
    }

    private fun objectValue(value: Any): ReferenceValue {
        return when (value) {
            is StructReference -> ReferenceValue.Struct(value)
            is ArrayReference -> ReferenceValue.Array(value)
            is I31Reference -> ReferenceValue.I31(value)
            is FunctionReference -> ReferenceValue.Function(value)
            is ExceptionReference -> ReferenceValue.Exception(value)
            else -> ReferenceValue.Extern(value)
        }
    }

    private fun bottom(heap: HeapType): HeapType {
        return when (heap) {
            AbstractHeapType.Func, AbstractHeapType.NoFunc -> AbstractHeapType.NoFunc
            AbstractHeapType.Extern, AbstractHeapType.NoExtern -> AbstractHeapType.NoExtern
            AbstractHeapType.Exception, AbstractHeapType.NoException -> AbstractHeapType.NoException
            is ConcreteHeapType.Defined if heap.definedType.isFunction() -> AbstractHeapType.NoFunc
            else -> AbstractHeapType.None
        }
    }

    private fun DefinedType.isFunction(): Boolean {
        return recursiveType.subTypes[recursiveTypeIndex].compositeType is CompositeType.Function
    }
}
