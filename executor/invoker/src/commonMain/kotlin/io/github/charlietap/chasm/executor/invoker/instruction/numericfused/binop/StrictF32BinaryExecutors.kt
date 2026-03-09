package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.invoker.ext.copySign
import io.github.charlietap.chasm.executor.invoker.ext.max
import io.github.charlietap.chasm.executor.invoker.ext.min
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun F32AddExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32AddIi,
) = executeF32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left + right }

internal inline fun F32AddExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32AddIs,
) = executeF32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left + right }

internal inline fun F32AddExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32AddSi,
) = executeF32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left + right }

internal inline fun F32AddExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32AddSs,
) = executeF32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left + right }

internal inline fun F32SubExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32SubIi,
) = executeF32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left - right }

internal inline fun F32SubExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32SubIs,
) = executeF32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left - right }

internal inline fun F32SubExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32SubSi,
) = executeF32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left - right }

internal inline fun F32SubExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32SubSs,
) = executeF32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left - right }

internal inline fun F32MulExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32MulIi,
) = executeF32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left * right }

internal inline fun F32MulExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32MulIs,
) = executeF32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left * right }

internal inline fun F32MulExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32MulSi,
) = executeF32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left * right }

internal inline fun F32MulExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32MulSs,
) = executeF32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left * right }

internal inline fun F32DivExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32DivIi,
) = executeF32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left / right }

internal inline fun F32DivExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32DivIs,
) = executeF32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left / right }

internal inline fun F32DivExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32DivSi,
) = executeF32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left / right }

internal inline fun F32DivExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32DivSs,
) = executeF32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left / right }

internal inline fun F32MinExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32MinIi,
) = executeF32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::min)

internal inline fun F32MinExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32MinIs,
) = executeF32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::min)

internal inline fun F32MinExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32MinSi,
) = executeF32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::min)

internal inline fun F32MinExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32MinSs,
) = executeF32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::min)

internal inline fun F32MaxExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32MaxIi,
) = executeF32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::max)

internal inline fun F32MaxExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32MaxIs,
) = executeF32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::max)

internal inline fun F32MaxExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32MaxSi,
) = executeF32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::max)

internal inline fun F32MaxExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32MaxSs,
) = executeF32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::max)

internal inline fun F32CopysignExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32CopysignIi,
) = executeF32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, Float::copySign)

internal inline fun F32CopysignExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32CopysignIs,
) = executeF32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, Float::copySign)

internal inline fun F32CopysignExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32CopysignSi,
) = executeF32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, Float::copySign)

internal inline fun F32CopysignExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32CopysignSs,
) = executeF32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, Float::copySign)
