package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun I64AddExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64AddIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left + right }

internal inline fun I64AddExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64AddIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left + right }

internal inline fun I64AddExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64AddSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left + right }

internal inline fun I64AddExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64AddSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left + right }

internal inline fun I64SubExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64SubIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left - right }

internal inline fun I64SubExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64SubIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left - right }

internal inline fun I64SubExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64SubSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left - right }

internal inline fun I64SubExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64SubSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left - right }

internal inline fun I64MulExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64MulIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left * right }

internal inline fun I64MulExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64MulIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left * right }

internal inline fun I64MulExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64MulSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left * right }

internal inline fun I64MulExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64MulSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left * right }

internal inline fun I64DivSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64DivSIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::strictI64DivS)

internal inline fun I64DivSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64DivSIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::strictI64DivS)

internal inline fun I64DivSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64DivSSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::strictI64DivS)

internal inline fun I64DivSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64DivSSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::strictI64DivS)

internal inline fun I64DivUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64DivUIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::strictI64DivU)

internal inline fun I64DivUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64DivUIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::strictI64DivU)

internal inline fun I64DivUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64DivUSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::strictI64DivU)

internal inline fun I64DivUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64DivUSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::strictI64DivU)

internal inline fun I64RemSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RemSIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::strictI64RemS)

internal inline fun I64RemSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RemSIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::strictI64RemS)

internal inline fun I64RemSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RemSSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::strictI64RemS)

internal inline fun I64RemSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RemSSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::strictI64RemS)

internal inline fun I64RemUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RemUIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right, ::strictI64RemU)

internal inline fun I64RemUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RemUIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot, ::strictI64RemU)

internal inline fun I64RemUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RemUSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right, ::strictI64RemU)

internal inline fun I64RemUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RemUSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot, ::strictI64RemU)

internal inline fun I64AndExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64AndIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left and right }

internal inline fun I64AndExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64AndIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left and right }

internal inline fun I64AndExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64AndSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left and right }

internal inline fun I64AndExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64AndSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left and right }

internal inline fun I64OrExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64OrIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left or right }

internal inline fun I64OrExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64OrIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left or right }

internal inline fun I64OrExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64OrSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left or right }

internal inline fun I64OrExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64OrSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left or right }

internal inline fun I64XorExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64XorIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left xor right }

internal inline fun I64XorExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64XorIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left xor right }

internal inline fun I64XorExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64XorSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left xor right }

internal inline fun I64XorExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64XorSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left xor right }

internal inline fun I64ShlExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ShlIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left shl right.toInt() }

internal inline fun I64ShlExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ShlIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left shl right.toInt() }

internal inline fun I64ShlExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ShlSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left shl right.toInt() }

internal inline fun I64ShlExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ShlSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left shl right.toInt() }

internal inline fun I64ShrSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ShrSIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left shr right.toInt() }

internal inline fun I64ShrSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ShrSIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left shr right.toInt() }

internal inline fun I64ShrSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ShrSSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left shr right.toInt() }

internal inline fun I64ShrSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ShrSSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left shr right.toInt() }

internal inline fun I64ShrUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ShrUIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left.toULong().shr(right.toInt()).toLong() }

internal inline fun I64ShrUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ShrUIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right ->
    left.toULong().shr(right.toInt()).toLong()
}

internal inline fun I64ShrUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ShrUSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right ->
    left.toULong().shr(right.toInt()).toLong()
}

internal inline fun I64ShrUExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ShrUSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right ->
    left.toULong().shr(right.toInt()).toLong()
}

internal inline fun I64RotlExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RotlIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left.rotateLeft(right.toInt()) }

internal inline fun I64RotlExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RotlIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left.rotateLeft(right.toInt()) }

internal inline fun I64RotlExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RotlSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left.rotateLeft(right.toInt()) }

internal inline fun I64RotlExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RotlSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left.rotateLeft(right.toInt()) }

internal inline fun I64RotrExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RotrIi,
) = executeI64BinaryIi(vstack, instruction.destinationSlot, instruction.left, instruction.right) { left, right -> left.rotateRight(right.toInt()) }

internal inline fun I64RotrExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RotrIs,
) = executeI64BinaryIs(vstack, instruction.destinationSlot, instruction.left, instruction.rightSlot) { left, right -> left.rotateRight(right.toInt()) }

internal inline fun I64RotrExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RotrSi,
) = executeI64BinarySi(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.right) { left, right -> left.rotateRight(right.toInt()) }

internal inline fun I64RotrExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64RotrSs,
) = executeI64BinarySs(vstack, instruction.destinationSlot, instruction.leftSlot, instruction.rightSlot) { left, right -> left.rotateRight(right.toInt()) }
