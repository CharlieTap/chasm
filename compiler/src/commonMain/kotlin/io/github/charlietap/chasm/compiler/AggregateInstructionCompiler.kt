package io.github.charlietap.chasm.compiler

import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.context.arrayType
import io.github.charlietap.chasm.compiler.context.data
import io.github.charlietap.chasm.compiler.context.element
import io.github.charlietap.chasm.compiler.context.runtimeType
import io.github.charlietap.chasm.compiler.context.structType
import io.github.charlietap.chasm.compiler.ext.valueType
import io.github.charlietap.chasm.compiler.instruction.emitAnyConvertExtern
import io.github.charlietap.chasm.compiler.instruction.emitArrayCopy
import io.github.charlietap.chasm.compiler.instruction.emitArrayFill
import io.github.charlietap.chasm.compiler.instruction.emitArrayGet
import io.github.charlietap.chasm.compiler.instruction.emitArrayInitData
import io.github.charlietap.chasm.compiler.instruction.emitArrayInitElement
import io.github.charlietap.chasm.compiler.instruction.emitArrayLen
import io.github.charlietap.chasm.compiler.instruction.emitArrayNew
import io.github.charlietap.chasm.compiler.instruction.emitArrayNewData
import io.github.charlietap.chasm.compiler.instruction.emitArrayNewDefault
import io.github.charlietap.chasm.compiler.instruction.emitArrayNewElement
import io.github.charlietap.chasm.compiler.instruction.emitArrayNewFixed
import io.github.charlietap.chasm.compiler.instruction.emitArraySet
import io.github.charlietap.chasm.compiler.instruction.emitExternConvertAny
import io.github.charlietap.chasm.compiler.instruction.emitI31Get
import io.github.charlietap.chasm.compiler.instruction.emitLocalSetStructGet
import io.github.charlietap.chasm.compiler.instruction.emitRefCastStructGet
import io.github.charlietap.chasm.compiler.instruction.emitRefI31
import io.github.charlietap.chasm.compiler.instruction.emitStructGet
import io.github.charlietap.chasm.compiler.instruction.emitStructGetStructGet
import io.github.charlietap.chasm.compiler.instruction.emitStructNew
import io.github.charlietap.chasm.compiler.instruction.emitStructNewDefault
import io.github.charlietap.chasm.compiler.instruction.emitStructSet
import io.github.charlietap.chasm.compiler.operand.Operand
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.isImmediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType

internal fun AggregateInstruction.isAllocating(): Boolean = when (this) {
    is AggregateInstruction.StructNew,
    is AggregateInstruction.StructNewDefault,
    is AggregateInstruction.ArrayNew,
    is AggregateInstruction.ArrayNewData,
    is AggregateInstruction.ArrayNewFixed,
    is AggregateInstruction.ArrayNewElement,
    is AggregateInstruction.ArrayNewDefault,
    -> true
    is AggregateInstruction.StructGet,
    is AggregateInstruction.StructGetSigned,
    is AggregateInstruction.StructGetUnsigned,
    is AggregateInstruction.StructSet,
    is AggregateInstruction.ArrayGet,
    is AggregateInstruction.ArrayGetSigned,
    is AggregateInstruction.ArrayGetUnsigned,
    is AggregateInstruction.ArraySet,
    AggregateInstruction.ArrayLen,
    is AggregateInstruction.ArrayFill,
    is AggregateInstruction.ArrayCopy,
    is AggregateInstruction.ArrayInitData,
    is AggregateInstruction.ArrayInitElement,
    AggregateInstruction.RefI31,
    AggregateInstruction.I31GetSigned,
    AggregateInstruction.I31GetUnsigned,
    AggregateInstruction.AnyConvertExtern,
    AggregateInstruction.ExternConvertAny,
    -> false
}

internal fun compileAggregateAccessChain(
    state: FunctionCompilationContext,
    first: Instruction,
    second: AggregateInstruction.StructGet,
    nextInstruction: Instruction?,
): Int? {
    val consumesNextInstruction = when (first) {
        is ReferenceInstruction.RefCast -> compileRefCastStructGet(
            state = state,
            cast = first,
            get = second,
            nextInstruction = nextInstruction,
        )
        is AggregateInstruction.StructGet -> {
            val field = state.compiler.structType(first.typeIndex).fields[first.fieldIndex.toInt()]
            if (field.valueType() !is ValueType.Reference) return null
            compileStructGetStructGet(
                state = state,
                first = first,
                second = second,
                nextInstruction = nextInstruction,
            )
        }
        is VariableInstruction.LocalTee -> compileLocalTeeStructGet(
            state = state,
            tee = first,
            get = second,
            nextInstruction = nextInstruction,
        ) ?: return null
        else -> return null
    }
    return if (consumesNextInstruction) 3 else 2
}

private fun compileRefCastStructGet(
    state: FunctionCompilationContext,
    cast: ReferenceInstruction.RefCast,
    get: AggregateInstruction.StructGet,
    nextInstruction: Instruction?,
): Boolean {
    val reference = state.pop()
    val resultType = state.compiler.structType(get.typeIndex).fields[get.fieldIndex.toInt()].valueType()
    val destination = destination(state, reference, nextInstruction)
    hydrateReferenceType(state, cast.referenceType)
    state.emitRefCastStructGet(
        referenceSlot = reference.let { source ->
            if (source.isImmediate) state.materialize(reference) else source.sourceSlot
        },
        destinationSlot = destination.slot,
        referenceType = cast.referenceType,
        fieldIndex = get.fieldIndex.toInt(),
    )
    completeDestination(state, resultType, destination)
    return destination.consumesNextInstruction
}

private fun compileStructGetStructGet(
    state: FunctionCompilationContext,
    first: AggregateInstruction.StructGet,
    second: AggregateInstruction.StructGet,
    nextInstruction: Instruction?,
): Boolean {
    val address = state.pop()
    val resultType = state.compiler.structType(second.typeIndex).fields[second.fieldIndex.toInt()].valueType()
    val destination = destination(state, address, nextInstruction)
    state.emitStructGetStructGet(
        addressSlot = address.let { source ->
            if (source.isImmediate) state.materialize(address) else source.sourceSlot
        },
        destinationSlot = destination.slot,
        firstFieldIndex = first.fieldIndex.toInt(),
        secondFieldIndex = second.fieldIndex.toInt(),
    )
    completeDestination(state, resultType, destination)
    return destination.consumesNextInstruction
}

private fun compileLocalTeeStructGet(
    state: FunctionCompilationContext,
    tee: VariableInstruction.LocalTee,
    get: AggregateInstruction.StructGet,
    nextInstruction: Instruction?,
): Boolean? {
    val reference = state.operands.lastOrNull() ?: return null
    val sourceSlot = when (reference.sourceKind) {
        OperandSourceKind.Local,
        OperandSourceKind.Frame,
        -> reference.sourceSlot
        else -> return null
    }
    val localIndex = tee.localIdx.toInt()
    val localSlot = state.layout.localSlot(localIndex)
    if (sourceSlot == localSlot) return null

    state.pop()
    state.preserveLocal(localIndex)
    val resultType = state.compiler.structType(get.typeIndex).fields[get.fieldIndex.toInt()].valueType()
    val destination = destination(state, reference, nextInstruction)
    state.emitLocalSetStructGet(
        sourceSlot = sourceSlot,
        localSlot = localSlot,
        destinationSlot = destination.slot,
        fieldIndex = get.fieldIndex.toInt(),
    )
    completeDestination(state, resultType, destination)
    return destination.consumesNextInstruction
}

internal fun compileAggregateInstruction(
    state: FunctionCompilationContext,
    instruction: AggregateInstruction,
    nextInstruction: Instruction?,
): Boolean = when (instruction) {
    is AggregateInstruction.StructNew -> {
        val type = state.compiler.structType(instruction.typeIndex)
        val fields = state.pop(type.fields.size)
        val destination = destination(state, fields.firstOrNull(), nextInstruction)
        state.emitStructNew(
            fieldSlots = fields.map(state::materialize),
            destinationSlot = destination.slot,
            rtt = state.compiler.runtimeType(instruction.typeIndex),
            type = type,
        )
        completeReferenceDestination(state, instruction.typeIndex.toInt(), destination)
        destination.consumesNextInstruction
    }
    is AggregateInstruction.StructNewDefault -> {
        val destination = destination(state, null, nextInstruction)
        state.emitStructNewDefault(
            destinationSlot = destination.slot,
            rtt = state.compiler.runtimeType(instruction.typeIndex),
            type = state.compiler.structType(instruction.typeIndex),
        )
        completeReferenceDestination(state, instruction.typeIndex.toInt(), destination)
        destination.consumesNextInstruction
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
        val resultType = state.compiler.structType(typeIndex).fields[fieldIndex.toInt()].valueType()
        val destination = destination(state, address, nextInstruction)
        state.emitStructGet(
            signed = when (instruction) {
                is AggregateInstruction.StructGet -> null
                is AggregateInstruction.StructGetSigned -> true
                is AggregateInstruction.StructGetUnsigned -> false
            },
            addressSlot = state.materialize(address),
            destinationSlot = destination.slot,
            fieldIndex = fieldIndex.toInt(),
        )
        completeDestination(state, resultType, destination)
        destination.consumesNextInstruction
    }
    is AggregateInstruction.StructSet -> {
        val value = state.pop()
        val address = state.pop()
        state.emitStructSet(
            value = value,
            addressSlot = state.materialize(address),
            fieldIndex = instruction.fieldIndex.toInt(),
        )
        false
    }
    is AggregateInstruction.ArrayNew -> {
        val size = state.pop()
        val value = state.pop()
        val destination = destination(state, value, nextInstruction)
        state.emitArrayNew(
            size = size,
            value = value,
            destinationSlot = destination.slot,
            rtt = state.compiler.runtimeType(instruction.typeIndex),
            type = state.compiler.arrayType(instruction.typeIndex),
        )
        completeReferenceDestination(state, instruction.typeIndex.toInt(), destination)
        destination.consumesNextInstruction
    }
    is AggregateInstruction.ArrayNewFixed -> {
        val values = state.pop(instruction.size.toInt())
        val destination = destination(state, values.firstOrNull(), nextInstruction)
        state.emitArrayNewFixed(
            valueSlots = values.map(state::materialize),
            destinationSlot = destination.slot,
            rtt = state.compiler.runtimeType(instruction.typeIndex),
            type = state.compiler.arrayType(instruction.typeIndex),
        )
        completeReferenceDestination(state, instruction.typeIndex.toInt(), destination)
        destination.consumesNextInstruction
    }
    is AggregateInstruction.ArrayNewDefault -> {
        val size = state.pop()
        val destination = destination(state, size, nextInstruction)
        state.emitArrayNewDefault(
            size = size,
            destinationSlot = destination.slot,
            rtt = state.compiler.runtimeType(instruction.typeIndex),
            type = state.compiler.arrayType(instruction.typeIndex),
        )
        completeReferenceDestination(state, instruction.typeIndex.toInt(), destination)
        destination.consumesNextInstruction
    }
    is AggregateInstruction.ArrayNewData -> {
        val length = state.pop()
        val sourceOffset = state.pop()
        val destination = destination(state, sourceOffset, nextInstruction)
        state.emitArrayNewData(
            sourceOffset = sourceOffset,
            length = length,
            destinationSlot = destination.slot,
            rtt = state.compiler.runtimeType(instruction.typeIndex),
            type = state.compiler.arrayType(instruction.typeIndex),
            data = state.compiler.data(instruction.dataIndex),
        )
        completeReferenceDestination(state, instruction.typeIndex.toInt(), destination)
        destination.consumesNextInstruction
    }
    is AggregateInstruction.ArrayNewElement -> {
        val length = state.pop()
        val sourceOffset = state.pop()
        val destination = destination(state, sourceOffset, nextInstruction)
        state.emitArrayNewElement(
            sourceOffset = sourceOffset,
            length = length,
            destinationSlot = destination.slot,
            rtt = state.compiler.runtimeType(instruction.typeIndex),
            type = state.compiler.arrayType(instruction.typeIndex),
            element = state.compiler.element(instruction.elementIndex),
        )
        completeReferenceDestination(state, instruction.typeIndex.toInt(), destination)
        destination.consumesNextInstruction
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
        val destination = destination(state, address, nextInstruction)
        state.emitArrayGet(
            signed = when (instruction) {
                is AggregateInstruction.ArrayGet -> null
                is AggregateInstruction.ArrayGetSigned -> true
                is AggregateInstruction.ArrayGetUnsigned -> false
            },
            addressSlot = state.materialize(address),
            field = field,
            destinationSlot = destination.slot,
        )
        completeDestination(state, state.compiler.arrayType(typeIndex).fieldType.valueType(), destination)
        destination.consumesNextInstruction
    }
    is AggregateInstruction.ArraySet -> {
        val value = state.pop()
        val field = state.pop()
        val address = state.pop()
        state.emitArraySet(value, field, state.materialize(address))
        false
    }
    AggregateInstruction.ArrayLen -> {
        val address = state.pop()
        val destination = destination(state, address, nextInstruction)
        state.emitArrayLen(state.materialize(address), destination.slot)
        completeDestination(state, I32_TYPE, destination)
        destination.consumesNextInstruction
    }
    is AggregateInstruction.ArrayFill -> {
        val elements = state.pop()
        val value = state.pop()
        val offset = state.pop()
        val address = state.pop()
        state.emitArrayFill(elements, value, offset, state.materialize(address))
        false
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
            state.materialize(sourceAddress),
            state.materialize(destinationAddress),
        )
        false
    }
    is AggregateInstruction.ArrayInitData -> {
        val elements = state.pop()
        val sourceOffset = state.pop()
        val destinationOffset = state.pop()
        val address = state.pop()
        state.emitArrayInitData(
            elements,
            sourceOffset,
            destinationOffset,
            state.materialize(address),
            state.compiler.data(instruction.dataIndex),
            state.compiler.arrayType(instruction.typeIndex),
        )
        false
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
            state.materialize(address),
            state.compiler.element(instruction.elementIndex),
        )
        false
    }
    AggregateInstruction.RefI31 -> {
        val value = state.pop()
        val destination = destination(state, value, nextInstruction)
        state.emitRefI31(value, destination.slot)
        completeDestination(state, I31_REFERENCE_TYPE, destination)
        destination.consumesNextInstruction
    }
    AggregateInstruction.I31GetSigned,
    AggregateInstruction.I31GetUnsigned,
    -> {
        val value = state.pop()
        val destination = destination(state, value, nextInstruction)
        state.emitI31Get(instruction == AggregateInstruction.I31GetSigned, state.materialize(value), destination.slot)
        completeDestination(state, I32_TYPE, destination)
        destination.consumesNextInstruction
    }
    AggregateInstruction.AnyConvertExtern,
    AggregateInstruction.ExternConvertAny,
    -> {
        val value = state.pop()
        val inputType = checkNotNull(value.type) as ValueType.Reference
        val destination = destination(state, value, nextInstruction)
        val outputHeapType = if (instruction == AggregateInstruction.AnyConvertExtern) AbstractHeapType.Any else AbstractHeapType.Extern
        val outputReferenceType = when (inputType.referenceType) {
            is ReferenceType.Ref -> ReferenceType.Ref(outputHeapType)
            is ReferenceType.RefNull -> ReferenceType.RefNull(outputHeapType)
        }
        if (instruction == AggregateInstruction.AnyConvertExtern) {
            state.emitAnyConvertExtern(state.materialize(value), destination.slot)
        } else {
            state.emitExternConvertAny(state.materialize(value), destination.slot)
        }
        completeDestination(state, ValueType.Reference(outputReferenceType), destination)
        destination.consumesNextInstruction
    }
}

private fun completeReferenceDestination(
    state: FunctionCompilationContext,
    typeIndex: Int,
    destination: Destination,
) {
    completeDestination(
        state,
        ValueType.Reference(ReferenceType.Ref(ConcreteHeapType.Defined(state.compiler.runtimeTypes[typeIndex].type))),
        destination,
    )
}

private val I31_REFERENCE_TYPE = ValueType.Reference(ReferenceType.Ref(AbstractHeapType.I31))
