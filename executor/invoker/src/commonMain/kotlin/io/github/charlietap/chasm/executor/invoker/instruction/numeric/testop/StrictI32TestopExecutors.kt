package io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeI32UnaryI
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeI32UnaryS
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun I32EqzExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32EqzI,
) = executeI32UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    if (operand == 0) 1 else 0
}

internal inline fun I32EqzExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32EqzS,
) = executeI32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    if (operand == 0) 1 else 0
}
