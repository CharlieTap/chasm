package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.embedding.value.VectorValue
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType

internal interface ValueBoxing {

    fun lower(
        values: List<ExecutionValue>,
        words: LongArray,
        references: Array<Any?>,
    ): String?

    fun raise(
        types: List<ValueType>,
        words: LongArray,
        references: Array<Any?>,
    ): List<ExecutionValue>

    fun word(value: NumberValue<*>): Long

    fun reference(value: ReferenceValue): Any?
}

internal class BoxedValues : ValueBoxing {

    override fun lower(
        values: List<ExecutionValue>,
        words: LongArray,
        references: Array<Any?>,
    ): String? {

        values.forEachIndexed { index, value ->
            when (value) {
                is NumberValue<*> -> {
                    words[index] = word(value)
                }
                is ReferenceValue -> {
                    references[index] = reference(value)
                }
                is VectorValue -> {
                    return "argument $index is a v128, which cannot cross the interpreter's embedding boundary"
                }
            }
        }

        return null
    }

    override fun raise(
        types: List<ValueType>,
        words: LongArray,
        references: Array<Any?>,
    ): List<ExecutionValue> {
        return types.mapIndexed { index, type ->
            when (type) {
                is ValueType.Number -> number(type.numberType, words[index])
                is ValueType.Reference -> referenceValue(type, references[index])
                else -> error("result $index has an unsupported type $type")
            }
        }
    }

    override fun word(value: NumberValue<*>): Long {
        return when (value) {
            is NumberValue.I32 -> value.value.toLong()
            is NumberValue.I64 -> value.value
            is NumberValue.F32 -> value.value.toRawBits().toLong()
            is NumberValue.F64 -> value.value.toRawBits()
        }
    }

    override fun reference(value: ReferenceValue): Any? {
        return when (value) {
            is ReferenceValue.Null -> null
            is ReferenceValue.Struct -> value.reference
            is ReferenceValue.Array -> value.reference
            is ReferenceValue.I31 -> value.reference
            is ReferenceValue.Function -> value.reference
            is ReferenceValue.Exception -> value.reference
            is ReferenceValue.Extern -> value.value
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

        val heapType = type.referenceType.heapType

        return when {
            value == null -> ReferenceValue.Null(bottom(heapType))
            heapType == AbstractHeapType.Extern || heapType == AbstractHeapType.NoExtern -> ReferenceValue.Extern(value)
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

    private fun bottom(heapType: HeapType): HeapType {
        return when (heapType) {
            AbstractHeapType.Func, AbstractHeapType.NoFunc -> AbstractHeapType.NoFunc
            AbstractHeapType.Extern, AbstractHeapType.NoExtern -> AbstractHeapType.NoExtern
            AbstractHeapType.Exception, AbstractHeapType.NoException -> AbstractHeapType.NoException
            is ConcreteHeapType.Defined if isFunction(heapType) -> AbstractHeapType.NoFunc
            else -> AbstractHeapType.None
        }
    }

    private fun isFunction(heapType: ConcreteHeapType.Defined): Boolean {

        val definedType = heapType.definedType

        return definedType.recursiveType.subTypes[definedType.recursiveTypeIndex].compositeType is CompositeType.Function
    }
}
