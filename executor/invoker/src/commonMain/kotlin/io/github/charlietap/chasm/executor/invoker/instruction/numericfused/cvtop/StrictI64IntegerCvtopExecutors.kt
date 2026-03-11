package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64ExtendI32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64ExtendI32SI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.operand.toLong())
}

internal inline fun I64ExtendI32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64ExtendI32SS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, vstack.getFrameSlot(instruction.operandSlot).toInt().toLong())
}

internal inline fun I64ExtendI32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64ExtendI32UI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.operand.toUInt().toLong())
}

internal inline fun I64ExtendI32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64ExtendI32US,
) {
    vstack.setFrameSlot(instruction.destinationSlot, vstack.getFrameSlot(instruction.operandSlot).toInt().toUInt().toLong())
}
