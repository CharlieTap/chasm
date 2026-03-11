package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I32AddExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32AddIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left + right }

internal inline fun I32AddExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32AddIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left + right }

internal inline fun I32AddExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32AddSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left + right }

internal inline fun I32AddExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32AddSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left + right }

internal inline fun I32SubExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32SubIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left - right }

internal inline fun I32SubExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32SubIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left - right }

internal inline fun I32SubExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32SubSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left - right }

internal inline fun I32SubExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32SubSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left - right }

internal inline fun I32MulExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32MulIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left * right }

internal inline fun I32MulExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32MulIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left * right }

internal inline fun I32MulExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32MulSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left * right }

internal inline fun I32MulExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32MulSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left * right }

internal inline fun I32DivSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32DivSIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::strictI32DivS)

internal inline fun I32DivSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32DivSIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::strictI32DivS)

internal inline fun I32DivSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32DivSSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::strictI32DivS)

internal inline fun I32DivSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32DivSSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::strictI32DivS)

internal inline fun I32DivUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32DivUIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::strictI32DivU)

internal inline fun I32DivUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32DivUIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::strictI32DivU)

internal inline fun I32DivUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32DivUSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::strictI32DivU)

internal inline fun I32DivUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32DivUSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::strictI32DivU)

internal inline fun I32RemSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RemSIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::strictI32RemS)

internal inline fun I32RemSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RemSIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::strictI32RemS)

internal inline fun I32RemSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RemSSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::strictI32RemS)

internal inline fun I32RemSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RemSSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::strictI32RemS)

internal inline fun I32RemUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RemUIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::strictI32RemU)

internal inline fun I32RemUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RemUIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::strictI32RemU)

internal inline fun I32RemUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RemUSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::strictI32RemU)

internal inline fun I32RemUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RemUSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::strictI32RemU)

internal inline fun I32AndExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32AndIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left and right }

internal inline fun I32AndExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32AndIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left and right }

internal inline fun I32AndExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32AndSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left and right }

internal inline fun I32AndExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32AndSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left and right }

internal inline fun I32OrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32OrIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left or right }

internal inline fun I32OrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32OrIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left or right }

internal inline fun I32OrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32OrSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left or right }

internal inline fun I32OrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32OrSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left or right }

internal inline fun I32XorExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32XorIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left xor right }

internal inline fun I32XorExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32XorIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left xor right }

internal inline fun I32XorExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32XorSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left xor right }

internal inline fun I32XorExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32XorSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left xor right }

internal inline fun I32ShlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32ShlIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left shl right }

internal inline fun I32ShlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32ShlIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left shl right }

internal inline fun I32ShlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32ShlSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left shl right }

internal inline fun I32ShlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32ShlSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left shl right }

internal inline fun I32ShrSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32ShrSIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left shr right }

internal inline fun I32ShrSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32ShrSIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left shr right }

internal inline fun I32ShrSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32ShrSSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left shr right }

internal inline fun I32ShrSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32ShrSSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left shr right }

internal inline fun I32ShrUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32ShrUIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left.toUInt().shr(right).toInt() }

internal inline fun I32ShrUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32ShrUIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left.toUInt().shr(right).toInt() }

internal inline fun I32ShrUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32ShrUSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left.toUInt().shr(right).toInt() }

internal inline fun I32ShrUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32ShrUSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left.toUInt().shr(right).toInt() }

internal inline fun I32RotlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RotlIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left.rotateLeft(right) }

internal inline fun I32RotlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RotlIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left.rotateLeft(right) }

internal inline fun I32RotlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RotlSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left.rotateLeft(right) }

internal inline fun I32RotlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RotlSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left.rotateLeft(right) }

internal inline fun I32RotrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RotrIi,
) = executeI32BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left.rotateRight(right) }

internal inline fun I32RotrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RotrIs,
) = executeI32BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left.rotateRight(right) }

internal inline fun I32RotrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RotrSi,
) = executeI32BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left.rotateRight(right) }

internal inline fun I32RotrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32RotrSs,
) = executeI32BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left.rotateRight(right) }
