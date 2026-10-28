package io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeI64UnaryI
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeI64UnaryS
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun I64EqzExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64EqzI,
) = executeI64UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    if (operand == 0L) 1L else 0L
}

internal inline fun I64EqzExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64EqzS,
) = executeI64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    if (operand == 0L) 1L else 0L
}
