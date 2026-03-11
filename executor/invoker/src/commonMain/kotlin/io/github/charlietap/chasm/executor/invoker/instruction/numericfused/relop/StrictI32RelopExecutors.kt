package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeI32BinaryIi
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeI32BinaryIs
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeI32BinarySi
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeI32BinarySs
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I32EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32EqIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right ->
    if (left == right) 1 else 0
}

internal inline fun I32EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32EqIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right ->
    if (left == right) 1 else 0
}

internal inline fun I32EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32EqSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right ->
    if (left == right) 1 else 0
}

internal inline fun I32EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32EqSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right ->
    if (left == right) 1 else 0
}

internal inline fun I32NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32NeIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right ->
    if (left != right) 1 else 0
}

internal inline fun I32NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32NeIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right ->
    if (left != right) 1 else 0
}

internal inline fun I32NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32NeSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right ->
    if (left != right) 1 else 0
}

internal inline fun I32NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32NeSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right ->
    if (left != right) 1 else 0
}

internal inline fun I32LtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LtSIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right ->
    if (left < right) 1 else 0
}

internal inline fun I32LtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LtSIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right ->
    if (left < right) 1 else 0
}

internal inline fun I32LtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LtSSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right ->
    if (left < right) 1 else 0
}

internal inline fun I32LtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LtSSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right ->
    if (left < right) 1 else 0
}

internal inline fun I32LtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LtUIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right ->
    if (left.toUInt() < right.toUInt()) 1 else 0
}

internal inline fun I32LtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LtUIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right ->
    if (left.toUInt() < right.toUInt()) 1 else 0
}

internal inline fun I32LtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LtUSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right ->
    if (left.toUInt() < right.toUInt()) 1 else 0
}

internal inline fun I32LtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LtUSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right ->
    if (left.toUInt() < right.toUInt()) 1 else 0
}

internal inline fun I32GtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GtSIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right ->
    if (left > right) 1 else 0
}

internal inline fun I32GtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GtSIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right ->
    if (left > right) 1 else 0
}

internal inline fun I32GtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GtSSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right ->
    if (left > right) 1 else 0
}

internal inline fun I32GtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GtSSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right ->
    if (left > right) 1 else 0
}

internal inline fun I32GtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GtUIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right ->
    if (left.toUInt() > right.toUInt()) 1 else 0
}

internal inline fun I32GtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GtUIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right ->
    if (left.toUInt() > right.toUInt()) 1 else 0
}

internal inline fun I32GtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GtUSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right ->
    if (left.toUInt() > right.toUInt()) 1 else 0
}

internal inline fun I32GtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GtUSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right ->
    if (left.toUInt() > right.toUInt()) 1 else 0
}

internal inline fun I32LeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LeSIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right ->
    if (left <= right) 1 else 0
}

internal inline fun I32LeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LeSIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right ->
    if (left <= right) 1 else 0
}

internal inline fun I32LeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LeSSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right ->
    if (left <= right) 1 else 0
}

internal inline fun I32LeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LeSSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right ->
    if (left <= right) 1 else 0
}

internal inline fun I32LeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LeUIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right ->
    if (left.toUInt() <= right.toUInt()) 1 else 0
}

internal inline fun I32LeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LeUIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right ->
    if (left.toUInt() <= right.toUInt()) 1 else 0
}

internal inline fun I32LeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LeUSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right ->
    if (left.toUInt() <= right.toUInt()) 1 else 0
}

internal inline fun I32LeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32LeUSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right ->
    if (left.toUInt() <= right.toUInt()) 1 else 0
}

internal inline fun I32GeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GeSIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right ->
    if (left >= right) 1 else 0
}

internal inline fun I32GeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GeSIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right ->
    if (left >= right) 1 else 0
}

internal inline fun I32GeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GeSSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right ->
    if (left >= right) 1 else 0
}

internal inline fun I32GeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GeSSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right ->
    if (left >= right) 1 else 0
}

internal inline fun I32GeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GeUIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right ->
    if (left.toUInt() >= right.toUInt()) 1 else 0
}

internal inline fun I32GeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GeUIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right ->
    if (left.toUInt() >= right.toUInt()) 1 else 0
}

internal inline fun I32GeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GeUSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right ->
    if (left.toUInt() >= right.toUInt()) 1 else 0
}

internal inline fun I32GeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32GeUSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right ->
    if (left.toUInt() >= right.toUInt()) 1 else 0
}
