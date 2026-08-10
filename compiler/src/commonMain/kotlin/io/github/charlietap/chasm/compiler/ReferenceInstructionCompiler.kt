package io.github.charlietap.chasm.compiler

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.context.function
import io.github.charlietap.chasm.compiler.context.global
import io.github.charlietap.chasm.compiler.instruction.emitGlobalGet
import io.github.charlietap.chasm.compiler.instruction.emitRefAsNonNull
import io.github.charlietap.chasm.compiler.instruction.emitRefCast
import io.github.charlietap.chasm.compiler.instruction.emitRefEq
import io.github.charlietap.chasm.compiler.instruction.emitRefFunc
import io.github.charlietap.chasm.compiler.instruction.emitRefIsNull
import io.github.charlietap.chasm.compiler.instruction.emitRefNull
import io.github.charlietap.chasm.compiler.instruction.emitRefTest
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType

internal fun compileGlobalGetInstruction(
    state: FunctionCompilationContext,
    instruction: VariableInstruction.GlobalGet,
    nextInstruction: Instruction?,
): Boolean {
    val global = state.compiler.global(instruction.globalIdx)
    val destination = destination(state, null, nextInstruction)
    state.emitGlobalGet(global, destination.slot)
    completeDestination(state, global.type.valueType, destination)
    return destination.consumesNextInstruction
}

internal fun compileReferenceInstruction(
    state: FunctionCompilationContext,
    instruction: ReferenceInstruction,
    nextInstruction: Instruction?,
): Boolean {
    val i32Type = I32_TYPE
    val destination: Destination
    val resultType: ValueType

    when (instruction) {
        is ReferenceInstruction.RefNull -> {
            resultType = ValueType.Reference(ReferenceType.RefNull(instruction.type))
            destination = destination(state, null, nextInstruction)
            state.emitRefNull(ReferenceValue.Null(instruction.type).toLong(), destination.slot)
        }
        is ReferenceInstruction.RefFunc -> {
            val function = state.compiler.function(instruction.funcIdx)
            resultType = ValueType.Reference(ReferenceType.Ref(ConcreteHeapType.Defined(function.rtt.type)))
            destination = destination(state, null, nextInstruction)
            val address = state.compiler.instance.functionAddresses[instruction.funcIdx.toInt()]
            state.emitRefFunc(ReferenceValue.Function(address).toLong(), destination.slot)
        }
        ReferenceInstruction.RefIsNull -> {
            val operand = state.pop()
            resultType = i32Type
            destination = destination(state, operand, nextInstruction)
            state.emitRefIsNull(state.materialize(operand), destination.slot)
        }
        ReferenceInstruction.RefAsNonNull -> {
            val operand = state.pop()
            val type = checkNotNull(operand.type) as ValueType.Reference
            resultType = ValueType.Reference(ReferenceType.Ref(type.referenceType.heapType))
            destination = destination(state, operand, nextInstruction)
            state.emitRefAsNonNull(state.materialize(operand), destination.slot)
        }
        ReferenceInstruction.RefEq -> {
            val second = state.pop()
            val first = state.pop()
            resultType = i32Type
            destination = destination(state, first, nextInstruction)
            state.emitRefEq(state.materialize(first), state.materialize(second), destination.slot)
        }
        is ReferenceInstruction.RefTest -> {
            val operand = state.pop()
            resultType = i32Type
            destination = destination(state, operand, nextInstruction)
            hydrateReferenceType(state, instruction.referenceType)
            state.emitRefTest(state.materialize(operand), destination.slot, instruction.referenceType)
        }
        is ReferenceInstruction.RefCast -> {
            val operand = state.pop()
            resultType = ValueType.Reference(instruction.referenceType)
            destination = destination(state, operand, nextInstruction)
            hydrateReferenceType(state, instruction.referenceType)
            state.emitRefCast(state.materialize(operand), destination.slot, instruction.referenceType)
        }
    }

    completeDestination(state, resultType, destination)
    return destination.consumesNextInstruction
}

internal fun hydrateReferenceType(
    state: FunctionCompilationContext,
    type: ReferenceType,
) {
    val heapType = type.heapType
    if (heapType is ConcreteHeapType.TypeIndex) {
        state.compiler.runtimeTypes[heapType.index].hydrate()
    }
}
