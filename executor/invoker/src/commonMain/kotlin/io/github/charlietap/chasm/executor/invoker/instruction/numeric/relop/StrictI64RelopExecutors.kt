package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64BinaryIi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64BinaryIs
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64BinarySi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64BinarySs
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun I64EqExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64EqIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::valueI64Eq)

internal inline fun I64EqExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64EqIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::valueI64Eq)

internal inline fun I64EqExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64EqSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::valueI64Eq)

internal inline fun I64EqExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64EqSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::valueI64Eq)

internal inline fun I64NeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64NeIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::valueI64Ne)

internal inline fun I64NeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64NeIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::valueI64Ne)

internal inline fun I64NeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64NeSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::valueI64Ne)

internal inline fun I64NeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64NeSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::valueI64Ne)

internal inline fun I64LtSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LtSIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::valueI64LtS)

internal inline fun I64LtSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LtSIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::valueI64LtS)

internal inline fun I64LtSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LtSSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::valueI64LtS)

internal inline fun I64LtSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LtSSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::valueI64LtS)

internal inline fun I64LtUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LtUIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::valueI64LtU)

internal inline fun I64LtUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LtUIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::valueI64LtU)

internal inline fun I64LtUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LtUSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::valueI64LtU)

internal inline fun I64LtUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LtUSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::valueI64LtU)

internal inline fun I64GtSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GtSIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::valueI64GtS)

internal inline fun I64GtSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GtSIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::valueI64GtS)

internal inline fun I64GtSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GtSSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::valueI64GtS)

internal inline fun I64GtSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GtSSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::valueI64GtS)

internal inline fun I64GtUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GtUIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::valueI64GtU)

internal inline fun I64GtUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GtUIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::valueI64GtU)

internal inline fun I64GtUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GtUSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::valueI64GtU)

internal inline fun I64GtUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GtUSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::valueI64GtU)

internal inline fun I64LeSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LeSIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::valueI64LeS)

internal inline fun I64LeSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LeSIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::valueI64LeS)

internal inline fun I64LeSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LeSSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::valueI64LeS)

internal inline fun I64LeSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LeSSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::valueI64LeS)

internal inline fun I64LeUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LeUIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::valueI64LeU)

internal inline fun I64LeUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LeUIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::valueI64LeU)

internal inline fun I64LeUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LeUSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::valueI64LeU)

internal inline fun I64LeUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64LeUSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::valueI64LeU)

internal inline fun I64GeSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GeSIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::valueI64GeS)

internal inline fun I64GeSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GeSIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::valueI64GeS)

internal inline fun I64GeSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GeSSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::valueI64GeS)

internal inline fun I64GeSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GeSSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::valueI64GeS)

internal inline fun I64GeUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GeUIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::valueI64GeU)

internal inline fun I64GeUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GeUIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::valueI64GeU)

internal inline fun I64GeUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GeUSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::valueI64GeU)

internal inline fun I64GeUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64GeUSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::valueI64GeU)
