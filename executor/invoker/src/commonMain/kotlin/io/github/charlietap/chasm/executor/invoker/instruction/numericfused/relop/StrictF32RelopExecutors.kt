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
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun F32EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32EqIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::eq)

internal inline fun F32EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32EqIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::eq)

internal inline fun F32EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32EqSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::eq)

internal inline fun F32EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32EqSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::eq)

internal inline fun F32NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32NeIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::ne)

internal inline fun F32NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32NeIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::ne)

internal inline fun F32NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32NeSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::ne)

internal inline fun F32NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32NeSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::ne)

internal inline fun F32LtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32LtIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::lt)

internal inline fun F32LtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32LtIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::lt)

internal inline fun F32LtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32LtSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::lt)

internal inline fun F32LtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32LtSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::lt)

internal inline fun F32GtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32GtIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::gt)

internal inline fun F32GtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32GtIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::gt)

internal inline fun F32GtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32GtSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::gt)

internal inline fun F32GtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32GtSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::gt)

internal inline fun F32LeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32LeIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::le)

internal inline fun F32LeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32LeIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::le)

internal inline fun F32LeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32LeSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::le)

internal inline fun F32LeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32LeSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::le)

internal inline fun F32GeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32GeIi,
) = executeF32RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::ge)

internal inline fun F32GeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32GeIs,
) = executeF32RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::ge)

internal inline fun F32GeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32GeSi,
) = executeF32RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::ge)

internal inline fun F32GeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32GeSs,
) = executeF32RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::ge)
