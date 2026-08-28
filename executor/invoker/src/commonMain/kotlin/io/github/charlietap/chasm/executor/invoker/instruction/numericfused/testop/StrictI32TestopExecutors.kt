package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.testop

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.executeI32UnaryI
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.executeI32UnaryS
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun I32EqzExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32EqzI,
) = executeI32UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    if (operand == 0) 1 else 0
}

internal inline fun I32EqzExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32EqzS,
) = executeI32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    if (operand == 0) 1 else 0
}
