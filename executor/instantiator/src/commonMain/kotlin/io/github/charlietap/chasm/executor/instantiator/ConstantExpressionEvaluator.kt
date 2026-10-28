package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.declarations.ConstantExpression
import io.github.charlietap.chasm.declarations.ConstantInstruction
import io.github.charlietap.chasm.declarations.ModuleTypeResolver
import io.github.charlietap.chasm.executor.instantiator.ext.functionAddress
import io.github.charlietap.chasm.executor.instantiator.ext.globalAddress
import io.github.charlietap.chasm.gc.GuestHeapOutOfMemoryException
import io.github.charlietap.chasm.runtime.encoder.HeapTypeEncoder
import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_FUNCTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_I31
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.encoder.ReferenceValueEncoder
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.ext.default
import io.github.charlietap.chasm.runtime.ext.global
import io.github.charlietap.chasm.runtime.ext.isExternReference
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.toExternReference
import io.github.charlietap.chasm.runtime.ext.toReferenceValue
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.ext.asArrayType
import io.github.charlietap.chasm.type.ext.asStructType

typealias ConstantExpressionEvaluator = (Store, ModuleInstance, ModuleTypeResolver, Expression) -> Result<Long, InvocationError>

fun ConstantExpressionEvaluator(
    store: Store,
    instance: ModuleInstance,
    types: ModuleTypeResolver,
    expression: Expression,
): Result<Long, InvocationError> = ConstantExpressionEvaluator(store, instance, types, constantExpression(expression))

fun ConstantExpressionEvaluator(
    store: Store,
    instance: ModuleInstance,
    types: ModuleTypeResolver,
    expression: ConstantExpression,
): Result<Long, InvocationError> {
    return try {
        ConstantExpressionEvaluator(
            store = store,
            instance = instance,
            types = types,
            expression = expression,
            stack = ValueStack(),
        )
    } catch (_: GuestHeapOutOfMemoryException) {
        Err(InvocationError.GuestHeapOutOfMemory)
    }
}

internal fun ConstantExpressionEvaluator(
    store: Store,
    instance: ModuleInstance,
    types: ModuleTypeResolver,
    expression: ConstantExpression,
    stack: ValueStack,
): Result<Long, InvocationError> {

    for (instruction in expression.instructions) {
        when (instruction) {
            is ConstantInstruction.I32Const -> stack.pushI32(instruction.value)
            is ConstantInstruction.I64Const -> stack.pushI64(instruction.value)
            is ConstantInstruction.F32Const -> stack.push(instruction.bits.toLong())
            is ConstantInstruction.F64Const -> stack.push(instruction.bits)
            ConstantInstruction.I32Add -> {
                val b = stack.popI32()
                val a = stack.popI32()
                stack.pushI32(a + b)
            }
            ConstantInstruction.I32Sub -> {
                val b = stack.popI32()
                val a = stack.popI32()
                stack.pushI32(a - b)
            }
            ConstantInstruction.I32Mul -> {
                val b = stack.popI32()
                val a = stack.popI32()
                stack.pushI32(a * b)
            }
            ConstantInstruction.I64Add -> {
                val b = stack.popI64()
                val a = stack.popI64()
                stack.pushI64(a + b)
            }
            ConstantInstruction.I64Sub -> {
                val b = stack.popI64()
                val a = stack.popI64()
                stack.pushI64(a - b)
            }
            ConstantInstruction.I64Mul -> {
                val b = stack.popI64()
                val a = stack.popI64()
                stack.pushI64(a * b)
            }
            is ConstantInstruction.V128Const -> stack.push(0L)
            is ConstantInstruction.RefNull -> {
                val encoded = (HeapTypeEncoder(instruction.type).toLong() shl RV_SHIFT_BITS) or RV_TYPE_NULL
                stack.push(encoded)
            }
            is ConstantInstruction.RefFunc -> {
                val address = instance.functionAddress(instruction.functionIndex).getOrElse { return Err(it) }
                val encoded = (address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_FUNCTION
                stack.push(encoded)
            }
            is ConstantInstruction.GlobalGet -> {
                val address = instance.globalAddress(instruction.globalIndex).getOrElse { return Err(it) }
                val value = store.global(address).value
                stack.push(value)
            }
            is ConstantInstruction.StructNew -> {
                val rtt = instance.runtimeTypes[instruction.typeIndex]
                val structType = DefinedTypeExpander(definedType(types, instruction.typeIndex)).asStructType()
                store.heap.allocateStructFromStack(rtt, structType.fields.size, stack)
            }
            is ConstantInstruction.StructNewDefault -> {
                val rtt = instance.runtimeTypes[instruction.typeIndex]
                val structType = DefinedTypeExpander(definedType(types, instruction.typeIndex)).asStructType()
                val fields = LongArray(structType.fields.size) { idx ->
                    structType.fields[idx].default()
                }
                stack.push(store.heap.allocateStruct(rtt, fields))
            }
            is ConstantInstruction.ArrayNew -> {
                val rtt = instance.runtimeTypes[instruction.typeIndex]
                val size = stack.popI32()
                val value = stack.pop()
                stack.push(store.heap.allocateArrayFilled(rtt, size, value))
            }
            is ConstantInstruction.ArrayNewDefault -> {
                val rtt = instance.runtimeTypes[instruction.typeIndex]
                val arrayType = DefinedTypeExpander(definedType(types, instruction.typeIndex)).asArrayType()
                val size = stack.popI32()
                val defaultValue = arrayType.fieldType.default()
                stack.push(store.heap.allocateArrayFilled(rtt, size, defaultValue))
            }
            is ConstantInstruction.ArrayNewFixed -> {
                val rtt = instance.runtimeTypes[instruction.typeIndex]
                store.heap.allocateArrayFromStack(rtt, instruction.size, stack)
            }
            ConstantInstruction.RefI31 -> {
                val value = stack.popI32()
                val wrapped = (value.toUInt() and 0x7FFFFFFFu).toLong()
                stack.push((wrapped shl RV_SHIFT_BITS) or RV_TYPE_I31)
            }
            ConstantInstruction.AnyConvertExtern -> {
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
            ConstantInstruction.ExternConvertAny -> {
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
        }
    }

    return if (stack.sp > 0) {
        Ok(stack.pop())
    } else {
        Err(InvocationError.MissingStackValue)
    }
}

private fun definedType(
    types: ModuleTypeResolver,
    typeIndex: Int,
): DefinedType =
    types.definedType(Index.TypeIndex(typeIndex.toUInt()))

private fun constantExpression(
    expression: Expression,
): ConstantExpression = ConstantExpression(
    expression.instructions.mapNotNull { instruction ->
        when (instruction) {
            is NumericInstruction.I32Const -> ConstantInstruction.I32Const(instruction.value)
            is NumericInstruction.I64Const -> ConstantInstruction.I64Const(instruction.value)
            is NumericInstruction.F32Const -> ConstantInstruction.F32Const(instruction.bits)
            is NumericInstruction.F64Const -> ConstantInstruction.F64Const(instruction.bits)
            is NumericInstruction.I32Add -> ConstantInstruction.I32Add
            is NumericInstruction.I32Sub -> ConstantInstruction.I32Sub
            is NumericInstruction.I32Mul -> ConstantInstruction.I32Mul
            is NumericInstruction.I64Add -> ConstantInstruction.I64Add
            is NumericInstruction.I64Sub -> ConstantInstruction.I64Sub
            is NumericInstruction.I64Mul -> ConstantInstruction.I64Mul
            is VectorInstruction.V128Const -> ConstantInstruction.V128Const(low = 0, high = 0)
            is VariableInstruction.GlobalGet -> ConstantInstruction.GlobalGet(instruction.globalIdx.position())
            is ReferenceInstruction.RefNull -> ConstantInstruction.RefNull(instruction.type)
            is ReferenceInstruction.RefFunc -> ConstantInstruction.RefFunc(instruction.funcIdx.position())
            is AggregateInstruction.StructNew -> ConstantInstruction.StructNew(instruction.typeIndex.position())
            is AggregateInstruction.StructNewDefault -> ConstantInstruction.StructNewDefault(instruction.typeIndex.position())
            is AggregateInstruction.ArrayNew -> ConstantInstruction.ArrayNew(instruction.typeIndex.position())
            is AggregateInstruction.ArrayNewDefault -> ConstantInstruction.ArrayNewDefault(instruction.typeIndex.position())
            is AggregateInstruction.ArrayNewFixed -> ConstantInstruction.ArrayNewFixed(
                typeIndex = instruction.typeIndex.position(),
                size = instruction.size.toInt(),
            )
            is AggregateInstruction.RefI31 -> ConstantInstruction.RefI31
            is AggregateInstruction.AnyConvertExtern -> ConstantInstruction.AnyConvertExtern
            is AggregateInstruction.ExternConvertAny -> ConstantInstruction.ExternConvertAny
            else -> null
        }
    },
)

private fun Index.position(): Int = idx.toInt()
