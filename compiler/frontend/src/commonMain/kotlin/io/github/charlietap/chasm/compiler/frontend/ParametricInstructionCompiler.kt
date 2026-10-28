package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.instruction.emitSelect
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSourceKind

internal fun compileSelectInstruction(
    state: FunctionCompilationContext,
) {
    val condition = state.pop()
    val second = state.pop()
    val first = state.pop()
    if (condition.sourceKind == OperandSourceKind.I32Immediate) {
        state.push(if (condition.sourceBits != 0L) first else second)
        return
    }
    val resultType = checkNotNull(first.type) {
        "select operand type is unavailable"
    }
    val destination = destination(state, first)
    state.emitSelect(
        condition = condition,
        first = first,
        second = second,
        destinationSlot = destination.slot,
    )
    completeDestination(state, resultType, destination)
}
