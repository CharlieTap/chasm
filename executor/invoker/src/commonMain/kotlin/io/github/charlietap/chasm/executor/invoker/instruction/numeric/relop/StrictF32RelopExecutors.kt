package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import io.github.charlietap.chasm.executor.invoker.ext.eq
import io.github.charlietap.chasm.executor.invoker.ext.ge
import io.github.charlietap.chasm.executor.invoker.ext.gt
import io.github.charlietap.chasm.executor.invoker.ext.le
import io.github.charlietap.chasm.executor.invoker.ext.lt
import io.github.charlietap.chasm.executor.invoker.ext.ne
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF32RelopIi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF32RelopIs
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF32RelopSi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF32RelopSs
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun F32EqExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32EqIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::eq)

internal inline fun F32EqExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32EqIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::eq)

internal inline fun F32EqExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32EqSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::eq)

internal inline fun F32EqExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32EqSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::eq)

internal inline fun F32NeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32NeIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::ne)

internal inline fun F32NeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32NeIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::ne)

internal inline fun F32NeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32NeSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::ne)

internal inline fun F32NeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32NeSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::ne)

internal inline fun F32LtExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32LtIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::lt)

internal inline fun F32LtExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32LtIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::lt)

internal inline fun F32LtExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32LtSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::lt)

internal inline fun F32LtExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32LtSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::lt)

internal inline fun F32GtExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32GtIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::gt)

internal inline fun F32GtExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32GtIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::gt)

internal inline fun F32GtExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32GtSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::gt)

internal inline fun F32GtExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32GtSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::gt)

internal inline fun F32LeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32LeIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::le)

internal inline fun F32LeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32LeIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::le)

internal inline fun F32LeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32LeSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::le)

internal inline fun F32LeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32LeSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::le)

internal inline fun F32GeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32GeIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::ge)

internal inline fun F32GeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32GeIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::ge)

internal inline fun F32GeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32GeSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::ge)

internal inline fun F32GeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32GeSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::ge)
