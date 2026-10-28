package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.context.arrayType
import io.github.charlietap.chasm.compiler.frontend.context.data
import io.github.charlietap.chasm.compiler.frontend.context.element
import io.github.charlietap.chasm.compiler.frontend.context.structType
import io.github.charlietap.chasm.compiler.frontend.context.type
import io.github.charlietap.chasm.compiler.frontend.ext.toCir
import io.github.charlietap.chasm.compiler.frontend.ext.valueType
import io.github.charlietap.chasm.compiler.frontend.instruction.emitAnyConvertExtern
import io.github.charlietap.chasm.compiler.frontend.instruction.emitArrayCopy
import io.github.charlietap.chasm.compiler.frontend.instruction.emitArrayFill
import io.github.charlietap.chasm.compiler.frontend.instruction.emitArrayGet
import io.github.charlietap.chasm.compiler.frontend.instruction.emitArrayInitData
import io.github.charlietap.chasm.compiler.frontend.instruction.emitArrayInitElement
import io.github.charlietap.chasm.compiler.frontend.instruction.emitArrayLen
import io.github.charlietap.chasm.compiler.frontend.instruction.emitArrayNew
import io.github.charlietap.chasm.compiler.frontend.instruction.emitArrayNewData
import io.github.charlietap.chasm.compiler.frontend.instruction.emitArrayNewDefault
import io.github.charlietap.chasm.compiler.frontend.instruction.emitArrayNewElement
import io.github.charlietap.chasm.compiler.frontend.instruction.emitArrayNewFixed
import io.github.charlietap.chasm.compiler.frontend.instruction.emitArraySet
import io.github.charlietap.chasm.compiler.frontend.instruction.emitExternConvertAny
import io.github.charlietap.chasm.compiler.frontend.instruction.emitI31Get
import io.github.charlietap.chasm.compiler.frontend.instruction.emitLocalSetStructGet
import io.github.charlietap.chasm.compiler.frontend.instruction.emitRefCastStructGet
import io.github.charlietap.chasm.compiler.frontend.instruction.emitRefI31
import io.github.charlietap.chasm.compiler.frontend.instruction.emitStructGet
import io.github.charlietap.chasm.compiler.frontend.instruction.emitStructGetStructGet
import io.github.charlietap.chasm.compiler.frontend.instruction.emitStructNew
import io.github.charlietap.chasm.compiler.frontend.instruction.emitStructNewDefault
import io.github.charlietap.chasm.compiler.frontend.instruction.emitStructSet
import io.github.charlietap.chasm.compiler.frontend.operand.Operand
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSource
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.frontend.operand.isImmediate
import io.github.charlietap.chasm.compiler.frontend.operand.sourceSlot
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.bitWidth
import io.github.charlietap.chasm.cir.ReferenceType as CirReferenceType

internal fun compileAggregateAccessChain(
    state: FunctionCompilationContext,
    first: Any?,
    second: AggregateInstruction.StructGet,
): Boolean = when (first) {
    is ReferenceInstruction.RefCast -> {
        state.instructions.consume()
        compileRefCastStructGet(state = state, cast = first, get = second)
        true
    }
    is AggregateInstruction.StructGet -> {
        val field = state.compiler.structType(first.typeIndex).fields[first.fieldIndex.toInt()]
        if (field.valueType() is ValueType.Reference) {
            state.instructions.consume()
            compileStructGetStructGet(state = state, first = first, second = second)
            true
        } else {
            false
        }
    }
    is VariableInstruction.LocalTee -> compileLocalTeeStructGet(state = state, tee = first, get = second)
    else -> false
}

private fun compileRefCastStructGet(
    state: FunctionCompilationContext,
    cast: ReferenceInstruction.RefCast,
    get: AggregateInstruction.StructGet,
) {
    val reference = state.pop()
    val resultType = state.compiler.structType(get.typeIndex).fields[get.fieldIndex.toInt()].valueType()
    val destination = destination(state, reference)
    state.emitRefCastStructGet(
        referenceSlot = reference.let { source ->
            if (source.isImmediate) state.materialize(reference) else source.sourceSlot
        },
        destinationSlot = destination.slot,
        typeTest = cast.referenceType.toCir(),
        type = state.compiler.type(get.typeIndex),
        fieldIndex = get.fieldIndex.toInt(),
    )
    completeDestination(state, resultType, destination)
}

private fun compileStructGetStructGet(
    state: FunctionCompilationContext,
    first: AggregateInstruction.StructGet,
    second: AggregateInstruction.StructGet,
) {
    val address = state.pop()
    val resultType = state.compiler.structType(second.typeIndex).fields[second.fieldIndex.toInt()].valueType()
    val destination = destination(state, address)
    state.emitStructGetStructGet(
        addressSlot = address.let { source ->
            if (source.isImmediate) state.materialize(address) else source.sourceSlot
        },
        destinationSlot = destination.slot,
        firstType = state.compiler.type(first.typeIndex),
        firstFieldIndex = first.fieldIndex.toInt(),
        secondType = state.compiler.type(second.typeIndex),
        secondFieldIndex = second.fieldIndex.toInt(),
    )
    completeDestination(state, resultType, destination)
}

private fun compileLocalTeeStructGet(
    state: FunctionCompilationContext,
    tee: VariableInstruction.LocalTee,
    get: AggregateInstruction.StructGet,
): Boolean {
    val reference = state.operands.lastOrNull() ?: return false
    val sourceSlot = when (reference.sourceKind) {
        OperandSourceKind.Local,
        OperandSourceKind.Frame,
        -> reference.sourceSlot
        else -> return false
    }
    val localIndex = tee.localIdx.toInt()
    val localSlot = state.layout.localSlot(localIndex)
    if (sourceSlot == localSlot) return false

    state.instructions.consume()
    state.pop()
    state.preserveLocal(localIndex)
    val resultType = state.compiler.structType(get.typeIndex).fields[get.fieldIndex.toInt()].valueType()
    val destination = destination(state, reference)
    state.emitLocalSetStructGet(
        sourceSlot = sourceSlot,
        localSlot = localSlot,
        destinationSlot = destination.slot,
        type = state.compiler.type(get.typeIndex),
        fieldIndex = get.fieldIndex.toInt(),
    )
    completeDestination(state, resultType, destination)
    return true
}

internal fun compileAggregateInstruction(
    state: FunctionCompilationContext,
    instruction: AggregateInstruction,
): Unit = when (instruction) {
    is AggregateInstruction.StructNew -> {
        val type = state.compiler.structType(instruction.typeIndex)
        val fields = state.pop(type.fields.size)
        val destination = destination(state, fields.firstOrNull())
        val contiguousSource = state.contiguousFrameSourceOrNull(fields)
        val materializedFields = fields.isNotEmpty() && contiguousSource == null
        val firstFieldSlot = contiguousSource ?: if (fields.isEmpty()) {
            destination.slot
        } else {
            state.materializeContiguous(fields)
        }
        state.emitStructNew(
            firstFieldSlot = firstFieldSlot,
            destinationSlot = destination.slot,
            type = state.compiler.type(instruction.typeIndex),
        )
        if (materializedFields) state.releaseContiguous(firstFieldSlot, fields.size)
        completeReferenceDestination(state, instruction.typeIndex.toInt(), destination)
    }
    is AggregateInstruction.StructNewDefault -> {
        val destination = destination(state, null)
        state.emitStructNewDefault(
            destinationSlot = destination.slot,
            type = state.compiler.type(instruction.typeIndex),
        )
        completeReferenceDestination(state, instruction.typeIndex.toInt(), destination)
    }
    is AggregateInstruction.StructGet,
    is AggregateInstruction.StructGetSigned,
    is AggregateInstruction.StructGetUnsigned,
    -> {
        val address = state.pop()
        val typeIndex = when (instruction) {
            is AggregateInstruction.StructGet -> instruction.typeIndex
            is AggregateInstruction.StructGetSigned -> instruction.typeIndex
            is AggregateInstruction.StructGetUnsigned -> instruction.typeIndex
        }
        val fieldIndex = when (instruction) {
            is AggregateInstruction.StructGet -> instruction.fieldIndex
            is AggregateInstruction.StructGetSigned -> instruction.fieldIndex
            is AggregateInstruction.StructGetUnsigned -> instruction.fieldIndex
        }
        val fieldType = state.compiler.structType(typeIndex).fields[fieldIndex.toInt()]
        val resultType = fieldType.valueType()
        val destination = destination(state, address)
        state.emitStructGet(
            signed = when (instruction) {
                is AggregateInstruction.StructGet -> null
                is AggregateInstruction.StructGetSigned -> true
                is AggregateInstruction.StructGetUnsigned -> false
            },
            packedType = (fieldType.storageType as? StorageType.Packed)?.type,
            addressSlot = state.operandSlot(address),
            destinationSlot = destination.slot,
            type = state.compiler.type(typeIndex),
            fieldIndex = fieldIndex.toInt(),
        )
        completeDestination(state, resultType, destination)
    }
    is AggregateInstruction.StructSet -> {
        val value = state.pop()
        val address = state.pop()
        state.emitStructSet(
            value = value,
            addressSlot = state.operandSlot(address),
            fieldIndex = instruction.fieldIndex.toInt(),
        )
    }
    is AggregateInstruction.ArrayNew -> {
        val size = state.pop()
        val value = state.pop()
        val destination = destination(state, value)
        state.emitArrayNew(
            size = size,
            value = value,
            destinationSlot = destination.slot,
            type = state.compiler.type(instruction.typeIndex),
        )
        completeReferenceDestination(state, instruction.typeIndex.toInt(), destination)
    }
    is AggregateInstruction.ArrayNewFixed -> {
        val length = instruction.size.toInt()
        val values = state.pop(length)
        val destination = destination(state, values.firstOrNull())
        val contiguousSource = state.contiguousFrameSourceOrNull(values)
        val materializedValues = values.isNotEmpty() && contiguousSource == null
        val firstElementSlot = contiguousSource ?: if (values.isEmpty()) {
            destination.slot
        } else {
            state.materializeContiguous(values)
        }
        state.emitArrayNewFixed(
            firstElementSlot = firstElementSlot,
            length = length,
            destinationSlot = destination.slot,
            type = state.compiler.type(instruction.typeIndex),
        )
        if (materializedValues) state.releaseContiguous(firstElementSlot, length)
        completeReferenceDestination(state, instruction.typeIndex.toInt(), destination)
    }
    is AggregateInstruction.ArrayNewDefault -> {
        val size = state.pop()
        val destination = destination(state, size)
        state.emitArrayNewDefault(
            size = size,
            destinationSlot = destination.slot,
            type = state.compiler.type(instruction.typeIndex),
        )
        completeReferenceDestination(state, instruction.typeIndex.toInt(), destination)
    }
    is AggregateInstruction.ArrayNewData -> {
        val length = state.pop()
        val sourceOffset = state.pop()
        val destination = destination(state, sourceOffset)
        val type = state.compiler.arrayType(instruction.typeIndex)
        val fieldWidthInBytes = checkNotNull(type.fieldType.bitWidth()) / 8
        state.emitArrayNewData(
            sourceOffset = sourceOffset,
            length = length,
            destinationSlot = destination.slot,
            type = state.compiler.type(instruction.typeIndex),
            data = state.compiler.data(instruction.dataIndex),
            fieldWidthInBytes = fieldWidthInBytes,
        )
        completeReferenceDestination(state, instruction.typeIndex.toInt(), destination)
    }
    is AggregateInstruction.ArrayNewElement -> {
        val length = state.pop()
        val sourceOffset = state.pop()
        val destination = destination(state, sourceOffset)
        state.emitArrayNewElement(
            sourceOffset = sourceOffset,
            length = length,
            destinationSlot = destination.slot,
            type = state.compiler.type(instruction.typeIndex),
            element = state.compiler.element(instruction.elementIndex),
        )
        completeReferenceDestination(state, instruction.typeIndex.toInt(), destination)
    }
    is AggregateInstruction.ArrayGet,
    is AggregateInstruction.ArrayGetSigned,
    is AggregateInstruction.ArrayGetUnsigned,
    -> {
        val field = state.pop()
        val address = state.pop()
        val typeIndex = when (instruction) {
            is AggregateInstruction.ArrayGet -> instruction.typeIndex
            is AggregateInstruction.ArrayGetSigned -> instruction.typeIndex
            is AggregateInstruction.ArrayGetUnsigned -> instruction.typeIndex
        }
        val destination = destination(state, address)
        val fieldType = state.compiler.arrayType(typeIndex).fieldType
        state.emitArrayGet(
            signed = when (instruction) {
                is AggregateInstruction.ArrayGet -> null
                is AggregateInstruction.ArrayGetSigned -> true
                is AggregateInstruction.ArrayGetUnsigned -> false
            },
            packedType = (fieldType.storageType as? StorageType.Packed)?.type,
            addressSlot = state.operandSlot(address),
            field = field,
            destinationSlot = destination.slot,
            type = state.compiler.type(typeIndex),
        )
        completeDestination(state, fieldType.valueType(), destination)
    }
    is AggregateInstruction.ArraySet -> {
        val value = state.pop()
        val field = state.pop()
        val address = state.pop()
        state.emitArraySet(value, field, state.operandSlot(address))
    }
    is AggregateInstruction.ArrayLen -> {
        val address = state.pop()
        val destination = destination(state, address)
        state.emitArrayLen(state.operandSlot(address), destination.slot)
        completeDestination(state, I32_TYPE, destination)
    }
    is AggregateInstruction.ArrayFill -> {
        val elements = state.pop()
        val value = state.pop()
        val offset = state.pop()
        val address = state.pop()
        state.emitArrayFill(elements, value, offset, state.operandSlot(address))
    }
    is AggregateInstruction.ArrayCopy -> {
        val elements = state.pop()
        val sourceOffset = state.pop()
        val sourceAddress = state.pop()
        val destinationOffset = state.pop()
        val destinationAddress = state.pop()
        state.emitArrayCopy(
            elements,
            sourceOffset,
            destinationOffset,
            state.operandSlot(sourceAddress),
            state.operandSlot(destinationAddress),
        )
    }
    is AggregateInstruction.ArrayInitData -> {
        val elements = state.pop()
        val sourceOffset = state.pop()
        val destinationOffset = state.pop()
        val address = state.pop()
        val elementByteWidth = checkNotNull(
            state.compiler.arrayType(instruction.typeIndex).fieldType.bitWidth(),
        ) / Byte.SIZE_BITS
        state.emitArrayInitData(
            elements,
            sourceOffset,
            destinationOffset,
            state.operandSlot(address),
            state.compiler.data(instruction.dataIndex),
            elementByteWidth,
        )
    }
    is AggregateInstruction.ArrayInitElement -> {
        val elements = state.pop()
        val sourceOffset = state.pop()
        val destinationOffset = state.pop()
        val address = state.pop()
        state.emitArrayInitElement(
            elements,
            sourceOffset,
            destinationOffset,
            state.operandSlot(address),
            state.compiler.element(instruction.elementIndex),
        )
    }
    is AggregateInstruction.RefI31 -> {
        val value = state.pop()
        val destination = destination(state, value)
        state.emitRefI31(value, destination.slot)
        completeDestination(state, I31_REFERENCE_TYPE, destination)
    }
    is AggregateInstruction.I31GetSigned,
    is AggregateInstruction.I31GetUnsigned,
    -> {
        val value = state.pop()
        val destination = destination(state, value)
        state.emitI31Get(instruction is AggregateInstruction.I31GetSigned, state.operandSlot(value), destination.slot)
        completeDestination(state, I32_TYPE, destination)
    }
    is AggregateInstruction.AnyConvertExtern,
    is AggregateInstruction.ExternConvertAny,
    -> {
        val value = state.pop()
        val inputType = checkNotNull(value.type) as ValueType.Reference
        val destination = destination(state, value)
        val outputHeapType = if (instruction is AggregateInstruction.AnyConvertExtern) AbstractHeapType.Any else AbstractHeapType.Extern
        val outputReferenceType = when (inputType.referenceType) {
            is ReferenceType.Ref -> ReferenceType.Ref(outputHeapType)
            is ReferenceType.RefNull -> ReferenceType.RefNull(outputHeapType)
        }
        if (instruction is AggregateInstruction.AnyConvertExtern) {
            state.emitAnyConvertExtern(state.operandSlot(value), destination.slot)
        } else {
            state.emitExternConvertAny(state.operandSlot(value), destination.slot)
        }
        completeDestination(state, ValueType.Reference(outputReferenceType), destination)
    }
}

private fun FunctionCompilationContext.operandSlot(operand: Operand): Int =
    if (operand.isImmediate) materialize(operand) else operand.sourceSlot

private fun completeReferenceDestination(
    state: FunctionCompilationContext,
    typeIndex: Int,
    destination: Destination,
) {
    completeDestination(
        state,
        ValueType.Reference(ReferenceType.Ref(ConcreteHeapType.Defined(state.compiler.module.definedTypes[typeIndex]))),
        destination,
    )
}

private val I31_REFERENCE_TYPE = ValueType.Reference(ReferenceType.Ref(AbstractHeapType.I31))
