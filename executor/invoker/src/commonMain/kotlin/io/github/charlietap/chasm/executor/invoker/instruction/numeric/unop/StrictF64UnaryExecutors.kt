package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.ceil
import io.github.charlietap.chasm.executor.invoker.ext.floor
import io.github.charlietap.chasm.executor.invoker.ext.nearest
import io.github.charlietap.chasm.executor.invoker.ext.sqrt
import io.github.charlietap.chasm.executor.invoker.ext.trunc
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack
import kotlin.math.absoluteValue

internal inline fun F64AbsExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64AbsI,
) = executeF64UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand -> operand.absoluteValue }

internal inline fun F64AbsExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64AbsS,
) = executeF64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand -> operand.absoluteValue }

internal inline fun F64NegExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64NegI,
) = executeF64UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand -> -operand }

internal inline fun F64NegExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64NegS,
) = executeF64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand -> -operand }

internal inline fun F64CeilExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64CeilI,
) = executeF64UnaryI(vstack, instruction.destinationSlot, instruction.operand, Double::ceil)

internal inline fun F64CeilExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64CeilS,
) = executeF64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, Double::ceil)

internal inline fun F64FloorExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64FloorI,
) = executeF64UnaryI(vstack, instruction.destinationSlot, instruction.operand, Double::floor)

internal inline fun F64FloorExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64FloorS,
) = executeF64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, Double::floor)

internal inline fun F64TruncExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64TruncI,
) = executeF64UnaryI(vstack, instruction.destinationSlot, instruction.operand, Double::trunc)

internal inline fun F64TruncExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64TruncS,
) = executeF64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, Double::trunc)

internal inline fun F64NearestExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64NearestI,
) = executeF64UnaryI(vstack, instruction.destinationSlot, instruction.operand, Double::nearest)

internal inline fun F64NearestExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64NearestS,
) = executeF64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, Double::nearest)

internal inline fun F64SqrtExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64SqrtI,
) = executeF64UnaryI(vstack, instruction.destinationSlot, instruction.operand, Double::sqrt)

internal inline fun F64SqrtExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64SqrtS,
) = executeF64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, Double::sqrt)
