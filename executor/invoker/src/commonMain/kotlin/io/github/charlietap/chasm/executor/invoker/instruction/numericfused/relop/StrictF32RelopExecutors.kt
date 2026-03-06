package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop

import io.github.charlietap.chasm.executor.invoker.ext.eq
import io.github.charlietap.chasm.executor.invoker.ext.ge
import io.github.charlietap.chasm.executor.invoker.ext.gt
import io.github.charlietap.chasm.executor.invoker.ext.le
import io.github.charlietap.chasm.executor.invoker.ext.lt
import io.github.charlietap.chasm.executor.invoker.ext.ne
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeF32RelopIi
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeF32RelopIs
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeF32RelopSi
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeF32RelopSs
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun F32EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32EqIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::eq)

internal inline fun F32EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32EqIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::eq)

internal inline fun F32EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32EqSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::eq)

internal inline fun F32EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32EqSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::eq)

internal inline fun F32NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32NeIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::ne)

internal inline fun F32NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32NeIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::ne)

internal inline fun F32NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32NeSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::ne)

internal inline fun F32NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32NeSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::ne)

internal inline fun F32LtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32LtIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::lt)

internal inline fun F32LtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32LtIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::lt)

internal inline fun F32LtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32LtSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::lt)

internal inline fun F32LtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32LtSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::lt)

internal inline fun F32GtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32GtIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::gt)

internal inline fun F32GtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32GtIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::gt)

internal inline fun F32GtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32GtSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::gt)

internal inline fun F32GtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32GtSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::gt)

internal inline fun F32LeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32LeIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::le)

internal inline fun F32LeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32LeIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::le)

internal inline fun F32LeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32LeSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::le)

internal inline fun F32LeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32LeSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::le)

internal inline fun F32GeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32GeIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::ge)

internal inline fun F32GeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32GeIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::ge)

internal inline fun F32GeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32GeSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::ge)

internal inline fun F32GeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32GeSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::ge)
