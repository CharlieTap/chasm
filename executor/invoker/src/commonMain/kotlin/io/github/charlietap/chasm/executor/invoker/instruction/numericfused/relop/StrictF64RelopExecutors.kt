package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop

import io.github.charlietap.chasm.executor.invoker.ext.eq
import io.github.charlietap.chasm.executor.invoker.ext.ge
import io.github.charlietap.chasm.executor.invoker.ext.gt
import io.github.charlietap.chasm.executor.invoker.ext.le
import io.github.charlietap.chasm.executor.invoker.ext.lt
import io.github.charlietap.chasm.executor.invoker.ext.ne
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeF64RelopIi
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeF64RelopIs
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeF64RelopSi
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.executeF64RelopSs
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun F64EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64EqIi,
) = executeF64RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Double::eq)

internal inline fun F64EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64EqIs,
) = executeF64RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Double::eq)

internal inline fun F64EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64EqSi,
) = executeF64RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Double::eq)

internal inline fun F64EqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64EqSs,
) = executeF64RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Double::eq)

internal inline fun F64NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64NeIi,
) = executeF64RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Double::ne)

internal inline fun F64NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64NeIs,
) = executeF64RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Double::ne)

internal inline fun F64NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64NeSi,
) = executeF64RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Double::ne)

internal inline fun F64NeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64NeSs,
) = executeF64RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Double::ne)

internal inline fun F64LtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64LtIi,
) = executeF64RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Double::lt)

internal inline fun F64LtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64LtIs,
) = executeF64RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Double::lt)

internal inline fun F64LtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64LtSi,
) = executeF64RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Double::lt)

internal inline fun F64LtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64LtSs,
) = executeF64RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Double::lt)

internal inline fun F64GtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64GtIi,
) = executeF64RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Double::gt)

internal inline fun F64GtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64GtIs,
) = executeF64RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Double::gt)

internal inline fun F64GtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64GtSi,
) = executeF64RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Double::gt)

internal inline fun F64GtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64GtSs,
) = executeF64RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Double::gt)

internal inline fun F64LeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64LeIi,
) = executeF64RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Double::le)

internal inline fun F64LeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64LeIs,
) = executeF64RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Double::le)

internal inline fun F64LeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64LeSi,
) = executeF64RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Double::le)

internal inline fun F64LeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64LeSs,
) = executeF64RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Double::le)

internal inline fun F64GeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64GeIi,
) = executeF64RelopIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Double::ge)

internal inline fun F64GeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64GeIs,
) = executeF64RelopIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Double::ge)

internal inline fun F64GeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64GeSi,
) = executeF64RelopSi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Double::ge)

internal inline fun F64GeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64GeSs,
) = executeF64RelopSs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Double::ge)
