package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64ClzExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ClzI,
) = executeI64UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    operand.countLeadingZeroBits().toLong()
}

internal inline fun I64ClzExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ClzS,
) = executeI64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    operand.countLeadingZeroBits().toLong()
}

internal inline fun I64CtzExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64CtzI,
) = executeI64UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    operand.countTrailingZeroBits().toLong()
}

internal inline fun I64CtzExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64CtzS,
) = executeI64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    operand.countTrailingZeroBits().toLong()
}

internal inline fun I64PopcntExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64PopcntI,
) = executeI64UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    operand.countOneBits().toLong()
}

internal inline fun I64PopcntExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64PopcntS,
) = executeI64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    operand.countOneBits().toLong()
}

internal inline fun I64Extend8SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Extend8SI,
) = executeI64UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    (operand and 0xFF).toByte().toLong()
}

internal inline fun I64Extend8SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Extend8SS,
) = executeI64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    (operand and 0xFF).toByte().toLong()
}

internal inline fun I64Extend16SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Extend16SI,
) = executeI64UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    (operand and 0xFFFF).toShort().toLong()
}

internal inline fun I64Extend16SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Extend16SS,
) = executeI64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    (operand and 0xFFFF).toShort().toLong()
}

internal inline fun I64Extend32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Extend32SI,
) = executeI64UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    ((operand and 0xFFFFFFFFL) shl 32) shr 32
}

internal inline fun I64Extend32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Extend32SS,
) = executeI64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    ((operand and 0xFFFFFFFFL) shl 32) shr 32
}
