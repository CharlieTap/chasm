package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
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

typealias ConstantExpressionEvaluator = (Store, ModuleInstance, ModuleTypeResolver, ConstantExpression) -> Result<Long, InvocationError>

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
