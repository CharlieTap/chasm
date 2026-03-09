package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.testop

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.executeI64UnaryI
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.executeI64UnaryS
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64EqzExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64EqzI,
) = executeI64UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    if (operand == 0L) 1L else 0L
}

internal inline fun I64EqzExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64EqzS,
) = executeI64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    if (operand == 0L) 1L else 0L
}
