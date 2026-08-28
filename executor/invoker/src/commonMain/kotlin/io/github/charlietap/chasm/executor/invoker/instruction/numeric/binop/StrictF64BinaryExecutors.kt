package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.invoker.ext.copySign
import io.github.charlietap.chasm.executor.invoker.ext.max
import io.github.charlietap.chasm.executor.invoker.ext.min
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun F64AddExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64AddIi,
) = executeF64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left + right }

internal inline fun F64AddExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64AddIs,
) = executeF64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left + right }

internal inline fun F64AddExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64AddSi,
) = executeF64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left + right }

internal inline fun F64AddExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64AddSs,
) = executeF64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left + right }

internal inline fun F64SubExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64SubIi,
) = executeF64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left - right }

internal inline fun F64SubExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64SubIs,
) = executeF64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left - right }

internal inline fun F64SubExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64SubSi,
) = executeF64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left - right }

internal inline fun F64SubExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64SubSs,
) = executeF64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left - right }

internal inline fun F64MulExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64MulIi,
) = executeF64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left * right }

internal inline fun F64MulExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64MulIs,
) = executeF64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left * right }

internal inline fun F64MulExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64MulSi,
) = executeF64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left * right }

internal inline fun F64MulExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64MulSs,
) = executeF64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left * right }

internal inline fun F64DivExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64DivIi,
) = executeF64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left / right }

internal inline fun F64DivExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64DivIs,
) = executeF64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left / right }

internal inline fun F64DivExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64DivSi,
) = executeF64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left / right }

internal inline fun F64DivExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64DivSs,
) = executeF64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left / right }

internal inline fun F64MinExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64MinIi,
) = executeF64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Double::min)

internal inline fun F64MinExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64MinIs,
) = executeF64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Double::min)

internal inline fun F64MinExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64MinSi,
) = executeF64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Double::min)

internal inline fun F64MinExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64MinSs,
) = executeF64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Double::min)

internal inline fun F64MaxExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64MaxIi,
) = executeF64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Double::max)

internal inline fun F64MaxExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64MaxIs,
) = executeF64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Double::max)

internal inline fun F64MaxExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64MaxSi,
) = executeF64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Double::max)

internal inline fun F64MaxExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64MaxSs,
) = executeF64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Double::max)

internal inline fun F64CopysignExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64CopysignIi,
) = executeF64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Double::copySign)

internal inline fun F64CopysignExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64CopysignIs,
) = executeF64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Double::copySign)

internal inline fun F64CopysignExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64CopysignSi,
) = executeF64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Double::copySign)

internal inline fun F64CopysignExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64CopysignSs,
) = executeF64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Double::copySign)
