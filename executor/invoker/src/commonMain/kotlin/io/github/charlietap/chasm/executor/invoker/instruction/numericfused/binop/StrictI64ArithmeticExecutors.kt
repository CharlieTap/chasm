package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64AddExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64AddIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left + right }

internal inline fun I64AddExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64AddIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left + right }

internal inline fun I64AddExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64AddSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left + right }

internal inline fun I64AddExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64AddSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left + right }

internal inline fun I64SubExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64SubIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left - right }

internal inline fun I64SubExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64SubIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left - right }

internal inline fun I64SubExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64SubSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left - right }

internal inline fun I64SubExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64SubSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left - right }

internal inline fun I64MulExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64MulIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left * right }

internal inline fun I64MulExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64MulIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left * right }

internal inline fun I64MulExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64MulSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left * right }

internal inline fun I64MulExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64MulSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left * right }

internal inline fun I64DivSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64DivSIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::strictI64DivS)

internal inline fun I64DivSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64DivSIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::strictI64DivS)

internal inline fun I64DivSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64DivSSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::strictI64DivS)

internal inline fun I64DivSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64DivSSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::strictI64DivS)

internal inline fun I64DivUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64DivUIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::strictI64DivU)

internal inline fun I64DivUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64DivUIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::strictI64DivU)

internal inline fun I64DivUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64DivUSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::strictI64DivU)

internal inline fun I64DivUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64DivUSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::strictI64DivU)

internal inline fun I64RemSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RemSIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::strictI64RemS)

internal inline fun I64RemSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RemSIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::strictI64RemS)

internal inline fun I64RemSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RemSSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::strictI64RemS)

internal inline fun I64RemSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RemSSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::strictI64RemS)

internal inline fun I64RemUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RemUIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::strictI64RemU)

internal inline fun I64RemUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RemUIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::strictI64RemU)

internal inline fun I64RemUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RemUSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::strictI64RemU)

internal inline fun I64RemUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RemUSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::strictI64RemU)

internal inline fun I64AndExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64AndIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left and right }

internal inline fun I64AndExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64AndIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left and right }

internal inline fun I64AndExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64AndSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left and right }

internal inline fun I64AndExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64AndSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left and right }

internal inline fun I64OrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64OrIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left or right }

internal inline fun I64OrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64OrIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left or right }

internal inline fun I64OrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64OrSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left or right }

internal inline fun I64OrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64OrSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left or right }

internal inline fun I64XorExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64XorIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left xor right }

internal inline fun I64XorExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64XorIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left xor right }

internal inline fun I64XorExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64XorSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left xor right }

internal inline fun I64XorExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64XorSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left xor right }

internal inline fun I64ShlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ShlIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left shl right.toInt() }

internal inline fun I64ShlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ShlIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left shl right.toInt() }

internal inline fun I64ShlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ShlSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left shl right.toInt() }

internal inline fun I64ShlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ShlSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left shl right.toInt() }

internal inline fun I64ShrSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ShrSIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left shr right.toInt() }

internal inline fun I64ShrSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ShrSIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left shr right.toInt() }

internal inline fun I64ShrSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ShrSSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left shr right.toInt() }

internal inline fun I64ShrSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ShrSSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left shr right.toInt() }

internal inline fun I64ShrUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ShrUIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left.toULong().shr(right.toInt()).toLong() }

internal inline fun I64ShrUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ShrUIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left.toULong().shr(right.toInt()).toLong() }

internal inline fun I64ShrUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ShrUSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left.toULong().shr(right.toInt()).toLong() }

internal inline fun I64ShrUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ShrUSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left.toULong().shr(right.toInt()).toLong() }

internal inline fun I64RotlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RotlIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left.rotateLeft(right.toInt()) }

internal inline fun I64RotlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RotlIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left.rotateLeft(right.toInt()) }

internal inline fun I64RotlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RotlSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left.rotateLeft(right.toInt()) }

internal inline fun I64RotlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RotlSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left.rotateLeft(right.toInt()) }

internal inline fun I64RotrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RotrIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left.rotateRight(right.toInt()) }

internal inline fun I64RotrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RotrIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left.rotateRight(right.toInt()) }

internal inline fun I64RotrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RotrSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left.rotateRight(right.toInt()) }

internal inline fun I64RotrExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RotrSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left.rotateRight(right.toInt()) }
