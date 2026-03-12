package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.executor.instantiator.ext.functionAddress
import io.github.charlietap.chasm.executor.instantiator.ext.globalAddress
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.NumericInstruction
import io.github.charlietap.chasm.ir.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ir.instruction.VariableInstruction
import io.github.charlietap.chasm.ir.instruction.VectorInstruction
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.encoder.HeapTypeEncoder
import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_FUNCTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_I31
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_STRUCT
import io.github.charlietap.chasm.runtime.encoder.ReferenceValueEncoder
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.ext.default
import io.github.charlietap.chasm.runtime.ext.global
import io.github.charlietap.chasm.runtime.ext.isExternReference
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.toExternReference
import io.github.charlietap.chasm.runtime.ext.toReferenceValue
import io.github.charlietap.chasm.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.StructInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.ext.asArrayType
import io.github.charlietap.chasm.type.ext.asStructType

typealias ConstantExpressionEvaluator = (Store, ModuleInstance, Expression) -> Result<Long, InvocationError>

fun ConstantExpressionEvaluator(
    store: Store,
    instance: ModuleInstance,
    expression: Expression,
): Result<Long, InvocationError> =
    ConstantExpressionEvaluator(
        store = store,
        instance = instance,
        expression = expression,
        stack = ValueStack(),
    )

internal fun ConstantExpressionEvaluator(
    store: Store,
    instance: ModuleInstance,
    expression: Expression,
    stack: ValueStack,
): Result<Long, InvocationError> {

    for (instruction in expression.instructions) {
        when (instruction) {
            is NumericInstruction.I32Const -> stack.pushI32(instruction.value)
            is NumericInstruction.I64Const -> stack.pushI64(instruction.value)
            is NumericInstruction.F32Const -> stack.push(instruction.bits.toLong())
            is NumericInstruction.F64Const -> stack.push(instruction.bits)
            is NumericInstruction.I32Add -> {
                val b = stack.popI32()
                val a = stack.popI32()
                stack.pushI32(a + b)
            }
            is NumericInstruction.I32Sub -> {
                val b = stack.popI32()
                val a = stack.popI32()
                stack.pushI32(a - b)
            }
            is NumericInstruction.I32Mul -> {
                val b = stack.popI32()
                val a = stack.popI32()
                stack.pushI32(a * b)
            }
            is NumericInstruction.I64Add -> {
                val b = stack.popI64()
                val a = stack.popI64()
                stack.pushI64(a + b)
            }
            is NumericInstruction.I64Sub -> {
                val b = stack.popI64()
                val a = stack.popI64()
                stack.pushI64(a - b)
            }
            is NumericInstruction.I64Mul -> {
                val b = stack.popI64()
                val a = stack.popI64()
                stack.pushI64(a * b)
            }
            is VectorInstruction.V128Const -> stack.push(0L)
            is ReferenceInstruction.RefNull -> {
                val encoded = (HeapTypeEncoder(instruction.type).toLong() shl RV_SHIFT_BITS) or RV_TYPE_NULL
                stack.push(encoded)
            }
            is ReferenceInstruction.RefFunc -> {
                val address = instance.functionAddress(instruction.funcIdx).getOrElse { return Err(it) }
                val encoded = (address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_FUNCTION
                stack.push(encoded)
            }
            is VariableInstruction.GlobalGet -> {
                val address = instance.globalAddress(instruction.globalIdx).getOrElse { return Err(it) }
                val value = store.global(address).value
                stack.push(value)
            }
            is AggregateInstruction.StructNew -> {
                val typeIndex = instruction.typeIndex.idx
                val rtt = instance.runtimeTypes[typeIndex]
                val structType = DefinedTypeExpander(instance.types[typeIndex]).asStructType()
                val size = structType.fields.size
                val fields = LongArray(size)
                for (i in size - 1 downTo 0) {
                    fields[i] = stack.pop()
                }
                val structInstance = StructInstance(rtt, structType, fields)
                val structAddress = allocateStruct(store, structInstance)
                stack.push((structAddress.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_STRUCT)
            }
            is AggregateInstruction.StructNewDefault -> {
                val typeIndex = instruction.typeIndex.idx
                val rtt = instance.runtimeTypes[typeIndex]
                val structType = DefinedTypeExpander(instance.types[typeIndex]).asStructType()
                val fields = LongArray(structType.fields.size) { idx ->
                    structType.fields[idx].default()
                }
                val structInstance = StructInstance(rtt, structType, fields)
                val structAddress = allocateStruct(store, structInstance)
                stack.push((structAddress.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_STRUCT)
            }
            is AggregateInstruction.ArrayNew -> {
                val typeIndex = instruction.typeIndex.idx
                val rtt = instance.runtimeTypes[typeIndex]
                val arrayType = DefinedTypeExpander(instance.types[typeIndex]).asArrayType()
                val size = stack.popI32()
                val value = stack.pop()
                val fields = LongArray(size) { value }
                val arrayInstance = ArrayInstance(rtt, arrayType, fields)
                val arrayAddress = allocateArray(store, arrayInstance)
                stack.push((arrayAddress.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_ARRAY)
            }
            is AggregateInstruction.ArrayNewDefault -> {
                val typeIndex = instruction.typeIndex.idx
                val rtt = instance.runtimeTypes[typeIndex]
                val arrayType = DefinedTypeExpander(instance.types[typeIndex]).asArrayType()
                val size = stack.popI32()
                val defaultValue = arrayType.fieldType.default()
                val fields = LongArray(size) { defaultValue }
                val arrayInstance = ArrayInstance(rtt, arrayType, fields)
                val arrayAddress = allocateArray(store, arrayInstance)
                stack.push((arrayAddress.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_ARRAY)
            }
            is AggregateInstruction.ArrayNewFixed -> {
                val typeIndex = instruction.typeIndex.idx
                val rtt = instance.runtimeTypes[typeIndex]
                val arrayType = DefinedTypeExpander(instance.types[typeIndex]).asArrayType()
                val length = instruction.size.toInt()
                val fields = LongArray(length)
                for (i in length - 1 downTo 0) {
                    fields[i] = stack.pop()
                }
                val arrayInstance = ArrayInstance(rtt, arrayType, fields)
                val arrayAddress = allocateArray(store, arrayInstance)
                stack.push((arrayAddress.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_ARRAY)
            }
            is AggregateInstruction.RefI31 -> {
                val value = stack.popI32()
                val wrapped = (value.toUInt() and 0x7FFFFFFFu).toLong()
                stack.push((wrapped shl RV_SHIFT_BITS) or RV_TYPE_I31)
            }
            is AggregateInstruction.AnyConvertExtern -> {
                val referenceValue = stack.pop()
                when {
                    referenceValue.isNullableReference() -> {
                        val encoded = (HeapTypeEncoder(AbstractHeapType.Any).toLong() shl RV_SHIFT_BITS) or RV_TYPE_NULL
                        stack.push(encoded)
                    }
                    referenceValue.isExternReference() -> {
                        val extern = referenceValue.toExternReference()
                        stack.push(ReferenceValueEncoder(extern.referenceValue))
                    }
                    else -> return Err(InvocationError.UnexpectedReferenceValue)
                }
            }
            is AggregateInstruction.ExternConvertAny -> {
                val referenceValue = stack.pop()
                when {
                    referenceValue.isNullableReference() -> {
                        val encoded = (HeapTypeEncoder(AbstractHeapType.Extern).toLong() shl RV_SHIFT_BITS) or RV_TYPE_NULL
                        stack.push(encoded)
                    }
                    else -> {
                        val inner = referenceValue.toReferenceValue()
                        stack.push(ReferenceValueEncoder(ReferenceValue.Extern(inner)))
                    }
                }
            }
            is AdminInstruction.EndBlock,
            is AdminInstruction.EndFunction,
            -> break

            else -> continue
        }
    }

    return if (stack.depth() > 0) {
        Ok(stack.pop())
    } else {
        Err(InvocationError.MissingStackValue)
    }
}

private fun allocateStruct(store: Store, instance: StructInstance): Address.Struct {
    val reference = store.heap.structReferencePool.removeFirstOrNull()
    if (reference != null) {
        store.structs[reference] = instance
    } else {
        store.structs.add(instance)
    }
    return Address.Struct(reference ?: (store.structs.size - 1))
}

private fun allocateArray(store: Store, instance: ArrayInstance): Address.Array {
    val reference = store.heap.arrayReferencePool.removeFirstOrNull()
    if (reference != null) {
        store.arrays[reference] = instance
    } else {
        store.arrays.add(instance)
    }
    return Address.Array(reference ?: (store.arrays.size - 1))
}
