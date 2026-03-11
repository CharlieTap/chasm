package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeI64BinaryIi
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeI64BinaryIs
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeI64BinarySi
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeI64BinarySs
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64EqIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> if (left == right) 1L else 0L }

internal inline fun I64EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64EqIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> if (left == right) 1L else 0L }

internal inline fun I64EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64EqSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> if (left == right) 1L else 0L }

internal inline fun I64EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64EqSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> if (left == right) 1L else 0L }

internal inline fun I64NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64NeIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> if (left != right) 1L else 0L }

internal inline fun I64NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64NeIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> if (left != right) 1L else 0L }

internal inline fun I64NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64NeSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> if (left != right) 1L else 0L }

internal inline fun I64NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64NeSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> if (left != right) 1L else 0L }

internal inline fun I64LtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LtSIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> if (left < right) 1L else 0L }

internal inline fun I64LtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LtSIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> if (left < right) 1L else 0L }

internal inline fun I64LtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LtSSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> if (left < right) 1L else 0L }

internal inline fun I64LtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LtSSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> if (left < right) 1L else 0L }

internal inline fun I64LtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LtUIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> if (left.toULong() < right.toULong()) 1L else 0L }

internal inline fun I64LtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LtUIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> if (left.toULong() < right.toULong()) 1L else 0L }

internal inline fun I64LtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LtUSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> if (left.toULong() < right.toULong()) 1L else 0L }

internal inline fun I64LtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LtUSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> if (left.toULong() < right.toULong()) 1L else 0L }

internal inline fun I64GtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GtSIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> if (left > right) 1L else 0L }

internal inline fun I64GtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GtSIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> if (left > right) 1L else 0L }

internal inline fun I64GtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GtSSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> if (left > right) 1L else 0L }

internal inline fun I64GtSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GtSSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> if (left > right) 1L else 0L }

internal inline fun I64GtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GtUIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> if (left.toULong() > right.toULong()) 1L else 0L }

internal inline fun I64GtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GtUIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> if (left.toULong() > right.toULong()) 1L else 0L }

internal inline fun I64GtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GtUSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> if (left.toULong() > right.toULong()) 1L else 0L }

internal inline fun I64GtUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GtUSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> if (left.toULong() > right.toULong()) 1L else 0L }

internal inline fun I64LeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LeSIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> if (left <= right) 1L else 0L }

internal inline fun I64LeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LeSIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> if (left <= right) 1L else 0L }

internal inline fun I64LeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LeSSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> if (left <= right) 1L else 0L }

internal inline fun I64LeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LeSSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> if (left <= right) 1L else 0L }

internal inline fun I64LeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LeUIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> if (left.toULong() <= right.toULong()) 1L else 0L }

internal inline fun I64LeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LeUIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> if (left.toULong() <= right.toULong()) 1L else 0L }

internal inline fun I64LeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LeUSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> if (left.toULong() <= right.toULong()) 1L else 0L }

internal inline fun I64LeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64LeUSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> if (left.toULong() <= right.toULong()) 1L else 0L }

internal inline fun I64GeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GeSIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> if (left >= right) 1L else 0L }

internal inline fun I64GeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GeSIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> if (left >= right) 1L else 0L }

internal inline fun I64GeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GeSSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> if (left >= right) 1L else 0L }

internal inline fun I64GeSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GeSSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> if (left >= right) 1L else 0L }

internal inline fun I64GeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GeUIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> if (left.toULong() >= right.toULong()) 1L else 0L }

internal inline fun I64GeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GeUIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> if (left.toULong() >= right.toULong()) 1L else 0L }

internal inline fun I64GeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GeUSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> if (left.toULong() >= right.toULong()) 1L else 0L }

internal inline fun I64GeUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64GeUSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> if (left.toULong() >= right.toULong()) 1L else 0L }
