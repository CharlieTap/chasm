package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.constant

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I32ConstExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32ConstS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.value.toLong())
}

internal inline fun I64ConstExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ConstS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.value)
}

internal inline fun F32ConstExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ConstS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.bits.toLong())
}

internal inline fun F64ConstExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64ConstS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.bits)
}
