package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.executor.invoker.ext.ceil
import io.github.charlietap.chasm.executor.invoker.ext.floor
import io.github.charlietap.chasm.executor.invoker.ext.nearest
import io.github.charlietap.chasm.executor.invoker.ext.sqrt
import io.github.charlietap.chasm.executor.invoker.ext.trunc
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import kotlin.math.absoluteValue

internal inline fun F32AbsExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32AbsI,
) = executeF32UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand -> operand.absoluteValue }

internal inline fun F32AbsExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32AbsS,
) = executeF32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand -> operand.absoluteValue }

internal inline fun F32NegExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32NegI,
) = executeF32UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand -> -operand }

internal inline fun F32NegExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32NegS,
) = executeF32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand -> -operand }

internal inline fun F32CeilExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32CeilI,
) = executeF32UnaryI(vstack, instruction.destinationSlot, instruction.operand, Float::ceil)

internal inline fun F32CeilExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32CeilS,
) = executeF32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, Float::ceil)

internal inline fun F32FloorExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32FloorI,
) = executeF32UnaryI(vstack, instruction.destinationSlot, instruction.operand, Float::floor)

internal inline fun F32FloorExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32FloorS,
) = executeF32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, Float::floor)

internal inline fun F32TruncExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32TruncI,
) = executeF32UnaryI(vstack, instruction.destinationSlot, instruction.operand, Float::trunc)

internal inline fun F32TruncExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32TruncS,
) = executeF32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, Float::trunc)

internal inline fun F32NearestExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32NearestI,
) = executeF32UnaryI(vstack, instruction.destinationSlot, instruction.operand, Float::nearest)

internal inline fun F32NearestExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32NearestS,
) = executeF32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, Float::nearest)

internal inline fun F32SqrtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32SqrtI,
) = executeF32UnaryI(vstack, instruction.destinationSlot, instruction.operand, Float::sqrt)

internal inline fun F32SqrtExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.F32SqrtS,
) = executeF32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, Float::sqrt)
