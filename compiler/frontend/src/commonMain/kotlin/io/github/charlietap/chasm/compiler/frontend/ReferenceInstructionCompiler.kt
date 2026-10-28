package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.context.function
import io.github.charlietap.chasm.compiler.frontend.context.global
import io.github.charlietap.chasm.compiler.frontend.context.globalType
import io.github.charlietap.chasm.compiler.frontend.ext.toCir
import io.github.charlietap.chasm.compiler.frontend.instruction.emitGlobalGet
import io.github.charlietap.chasm.compiler.frontend.instruction.emitRefAsNonNull
import io.github.charlietap.chasm.compiler.frontend.instruction.emitRefCast
import io.github.charlietap.chasm.compiler.frontend.instruction.emitRefEq
import io.github.charlietap.chasm.compiler.frontend.instruction.emitRefFunc
import io.github.charlietap.chasm.compiler.frontend.instruction.emitRefIsNull
import io.github.charlietap.chasm.compiler.frontend.instruction.emitRefNull
import io.github.charlietap.chasm.compiler.frontend.instruction.emitRefTest
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.cir.ReferenceType as CirReferenceType

internal fun compileGlobalGetInstruction(
    state: FunctionCompilationContext,
    instruction: VariableInstruction.GlobalGet,
) {
    val global = state.compiler.global(instruction.globalIdx)
    val destination = destination(state, null)
    state.emitGlobalGet(global, destination.slot)
    completeDestination(state, state.compiler.globalType(instruction.globalIdx).valueType, destination)
}

internal fun compileReferenceInstruction(
    state: FunctionCompilationContext,
    instruction: ReferenceInstruction,
) {
    val i32Type = I32_TYPE
    val destination: Destination
    val resultType: ValueType

    when (instruction) {
        is ReferenceInstruction.RefNull -> {
            resultType = ValueType.Reference(ReferenceType.RefNull(instruction.type))
            destination = destination(state, null)
            state.emitRefNull(instruction.type.toCir(), destination.slot)
        }
        is ReferenceInstruction.RefFunc -> {
            resultType = ValueType.Reference(
                ReferenceType.Ref(ConcreteHeapType.Defined(state.compiler.types.functionType(instruction.funcIdx))),
            )
            destination = destination(state, null)
            state.emitRefFunc(state.compiler.function(instruction.funcIdx), destination.slot)
        }
        is ReferenceInstruction.RefIsNull -> {
            val operand = state.pop()
            resultType = i32Type
            destination = destination(state, operand)
            state.emitRefIsNull(state.materialize(operand), destination.slot)
        }
        is ReferenceInstruction.RefAsNonNull -> {
            val operand = state.pop()
            val type = checkNotNull(operand.type) as ValueType.Reference
            resultType = ValueType.Reference(ReferenceType.Ref(type.referenceType.heapType))
            destination = destination(state, operand)
            state.emitRefAsNonNull(state.materialize(operand), destination.slot)
        }
        is ReferenceInstruction.RefEq -> {
            val second = state.pop()
            val first = state.pop()
            resultType = i32Type
            destination = destination(state, first)
            state.emitRefEq(state.materialize(first), state.materialize(second), destination.slot)
        }
        is ReferenceInstruction.RefTest -> {
            val operand = state.pop()
            resultType = i32Type
            destination = destination(state, operand)
            state.emitRefTest(
                state.materialize(operand),
                destination.slot,
                instruction.referenceType.toCir(),
            )
        }
        is ReferenceInstruction.RefCast -> {
            val operand = state.pop()
            resultType = ValueType.Reference(instruction.referenceType)
            destination = destination(state, operand)
            state.emitRefCast(
                state.materialize(operand),
                destination.slot,
                instruction.referenceType.toCir(),
            )
        }
    }

    completeDestination(state, resultType, destination)
}
