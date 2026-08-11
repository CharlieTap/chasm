package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32DivSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32DivUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32RemSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32RemUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32RotlExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32RotrExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32ShlExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32ShrSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32ShrUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

fun I32AddDispatcher(instruction: NumericSuperInstruction.I32AddIi) = I32BinaryIiDispatcher(
    instruction.left,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left + right }

fun I32AddDispatcher(instruction: NumericSuperInstruction.I32AddIs) = I32BinaryIsDispatcher(
    instruction.left,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left + right }

fun I32AddDispatcher(instruction: NumericSuperInstruction.I32AddSi) = I32BinarySiDispatcher(
    instruction.leftSlot,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left + right }

fun I32AddDispatcher(instruction: NumericSuperInstruction.I32AddSs) = I32BinarySsDispatcher(
    instruction.leftSlot,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left + right }

fun I32SubDispatcher(instruction: NumericSuperInstruction.I32SubIi) = I32BinaryIiDispatcher(
    instruction.left,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left - right }

fun I32SubDispatcher(instruction: NumericSuperInstruction.I32SubIs) = I32BinaryIsDispatcher(
    instruction.left,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left - right }

fun I32SubDispatcher(instruction: NumericSuperInstruction.I32SubSi) = I32BinarySiDispatcher(
    instruction.leftSlot,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left - right }

fun I32SubDispatcher(instruction: NumericSuperInstruction.I32SubSs) = I32BinarySsDispatcher(
    instruction.leftSlot,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left - right }

fun I32MulDispatcher(instruction: NumericSuperInstruction.I32MulIi) = I32BinaryIiDispatcher(
    instruction.left,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left * right }

fun I32MulDispatcher(instruction: NumericSuperInstruction.I32MulIs) = I32BinaryIsDispatcher(
    instruction.left,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left * right }

fun I32MulDispatcher(instruction: NumericSuperInstruction.I32MulSi) = I32BinarySiDispatcher(
    instruction.leftSlot,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left * right }

fun I32MulDispatcher(instruction: NumericSuperInstruction.I32MulSs) = I32BinarySsDispatcher(
    instruction.leftSlot,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left * right }

fun I32DivSDispatcher(instruction: NumericSuperInstruction.I32DivSIi) = dispatchInstruction(instruction, ::I32DivSExecutor)

fun I32DivSDispatcher(instruction: NumericSuperInstruction.I32DivSIs) = dispatchInstruction(instruction, ::I32DivSExecutor)

fun I32DivSDispatcher(instruction: NumericSuperInstruction.I32DivSSi) = dispatchInstruction(instruction, ::I32DivSExecutor)

fun I32DivSDispatcher(instruction: NumericSuperInstruction.I32DivSSs) = dispatchInstruction(instruction, ::I32DivSExecutor)

fun I32DivUDispatcher(instruction: NumericSuperInstruction.I32DivUIi) = dispatchInstruction(instruction, ::I32DivUExecutor)

fun I32DivUDispatcher(instruction: NumericSuperInstruction.I32DivUIs) = dispatchInstruction(instruction, ::I32DivUExecutor)

fun I32DivUDispatcher(instruction: NumericSuperInstruction.I32DivUSi) = dispatchInstruction(instruction, ::I32DivUExecutor)

fun I32DivUDispatcher(instruction: NumericSuperInstruction.I32DivUSs) = dispatchInstruction(instruction, ::I32DivUExecutor)

fun I32RemSDispatcher(instruction: NumericSuperInstruction.I32RemSIi) = dispatchInstruction(instruction, ::I32RemSExecutor)

fun I32RemSDispatcher(instruction: NumericSuperInstruction.I32RemSIs) = dispatchInstruction(instruction, ::I32RemSExecutor)

fun I32RemSDispatcher(instruction: NumericSuperInstruction.I32RemSSi) = dispatchInstruction(instruction, ::I32RemSExecutor)

fun I32RemSDispatcher(instruction: NumericSuperInstruction.I32RemSSs) = dispatchInstruction(instruction, ::I32RemSExecutor)

fun I32RemUDispatcher(instruction: NumericSuperInstruction.I32RemUIi) = dispatchInstruction(instruction, ::I32RemUExecutor)

fun I32RemUDispatcher(instruction: NumericSuperInstruction.I32RemUIs) = dispatchInstruction(instruction, ::I32RemUExecutor)

fun I32RemUDispatcher(instruction: NumericSuperInstruction.I32RemUSi) = dispatchInstruction(instruction, ::I32RemUExecutor)

fun I32RemUDispatcher(instruction: NumericSuperInstruction.I32RemUSs) = dispatchInstruction(instruction, ::I32RemUExecutor)

fun I32AndDispatcher(instruction: NumericSuperInstruction.I32AndIi) = I32BinaryIiDispatcher(
    instruction.left,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left and right }

fun I32AndDispatcher(instruction: NumericSuperInstruction.I32AndIs) = I32BinaryIsDispatcher(
    instruction.left,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left and right }

fun I32AndDispatcher(instruction: NumericSuperInstruction.I32AndSi) = I32BinarySiDispatcher(
    instruction.leftSlot,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left and right }

fun I32AndDispatcher(instruction: NumericSuperInstruction.I32AndSs) = I32BinarySsDispatcher(
    instruction.leftSlot,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left and right }

fun I32OrDispatcher(instruction: NumericSuperInstruction.I32OrIi) = I32BinaryIiDispatcher(
    instruction.left,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left or right }

fun I32OrDispatcher(instruction: NumericSuperInstruction.I32OrIs) = I32BinaryIsDispatcher(
    instruction.left,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left or right }

fun I32OrDispatcher(instruction: NumericSuperInstruction.I32OrSi) = I32BinarySiDispatcher(
    instruction.leftSlot,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left or right }

fun I32OrDispatcher(instruction: NumericSuperInstruction.I32OrSs) = I32BinarySsDispatcher(
    instruction.leftSlot,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left or right }

fun I32XorDispatcher(instruction: NumericSuperInstruction.I32XorIi) = I32BinaryIiDispatcher(
    instruction.left,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left xor right }

fun I32XorDispatcher(instruction: NumericSuperInstruction.I32XorIs) = I32BinaryIsDispatcher(
    instruction.left,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left xor right }

fun I32XorDispatcher(instruction: NumericSuperInstruction.I32XorSi) = I32BinarySiDispatcher(
    instruction.leftSlot,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left xor right }

fun I32XorDispatcher(instruction: NumericSuperInstruction.I32XorSs) = I32BinarySsDispatcher(
    instruction.leftSlot,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left xor right }

fun I32ShlDispatcher(instruction: NumericSuperInstruction.I32ShlIi) = dispatchInstruction(instruction, ::I32ShlExecutor)

fun I32ShlDispatcher(instruction: NumericSuperInstruction.I32ShlIs) = dispatchInstruction(instruction, ::I32ShlExecutor)

fun I32ShlDispatcher(
    instruction: NumericSuperInstruction.I32ShlSi,
): DispatchableInstruction {
    val leftSlot = instruction.leftSlot
    val right = instruction.right
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, _, _, nextIp ->
        val value = vstack.getFrameSlot(leftSlot).toInt() shl right
        vstack.setFrameSlot(destinationSlot, value.toLong())
        nextIp
    }
}

fun I32ShlDispatcher(instruction: NumericSuperInstruction.I32ShlSs) = dispatchInstruction(instruction, ::I32ShlExecutor)

fun I32ShrSDispatcher(instruction: NumericSuperInstruction.I32ShrSIi) = dispatchInstruction(instruction, ::I32ShrSExecutor)

fun I32ShrSDispatcher(instruction: NumericSuperInstruction.I32ShrSIs) = dispatchInstruction(instruction, ::I32ShrSExecutor)

fun I32ShrSDispatcher(
    instruction: NumericSuperInstruction.I32ShrSSi,
): DispatchableInstruction {
    val leftSlot = instruction.leftSlot
    val right = instruction.right
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, _, _, nextIp ->
        val value = vstack.getFrameSlot(leftSlot).toInt() shr right
        vstack.setFrameSlot(destinationSlot, value.toLong())
        nextIp
    }
}

fun I32ShrSDispatcher(instruction: NumericSuperInstruction.I32ShrSSs) = dispatchInstruction(instruction, ::I32ShrSExecutor)

fun I32ShrUDispatcher(instruction: NumericSuperInstruction.I32ShrUIi) = dispatchInstruction(instruction, ::I32ShrUExecutor)

fun I32ShrUDispatcher(instruction: NumericSuperInstruction.I32ShrUIs) = dispatchInstruction(instruction, ::I32ShrUExecutor)

fun I32ShrUDispatcher(
    instruction: NumericSuperInstruction.I32ShrUSi,
): DispatchableInstruction {
    val leftSlot = instruction.leftSlot
    val right = instruction.right
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, _, _, nextIp ->
        val value = vstack.getFrameSlot(leftSlot).toInt() ushr right
        vstack.setFrameSlot(destinationSlot, value.toLong())
        nextIp
    }
}

fun I32ShrUDispatcher(instruction: NumericSuperInstruction.I32ShrUSs) = dispatchInstruction(instruction, ::I32ShrUExecutor)

fun I32RotlDispatcher(instruction: NumericSuperInstruction.I32RotlIi) = dispatchInstruction(instruction, ::I32RotlExecutor)

fun I32RotlDispatcher(instruction: NumericSuperInstruction.I32RotlIs) = dispatchInstruction(instruction, ::I32RotlExecutor)

fun I32RotlDispatcher(instruction: NumericSuperInstruction.I32RotlSi) = dispatchInstruction(instruction, ::I32RotlExecutor)

fun I32RotlDispatcher(instruction: NumericSuperInstruction.I32RotlSs) = dispatchInstruction(instruction, ::I32RotlExecutor)

fun I32RotrDispatcher(instruction: NumericSuperInstruction.I32RotrIi) = dispatchInstruction(instruction, ::I32RotrExecutor)

fun I32RotrDispatcher(instruction: NumericSuperInstruction.I32RotrIs) = dispatchInstruction(instruction, ::I32RotrExecutor)

fun I32RotrDispatcher(instruction: NumericSuperInstruction.I32RotrSi) = dispatchInstruction(instruction, ::I32RotrExecutor)

fun I32RotrDispatcher(instruction: NumericSuperInstruction.I32RotrSs) = dispatchInstruction(instruction, ::I32RotrExecutor)

private inline fun I32BinaryIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
    crossinline operation: (Int, Int) -> Int,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
    vstack.setFrameSlot(destinationSlot, operation(left, right).toLong())
    nextIp
}

private inline fun I32BinaryIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
    crossinline operation: (Int, Int) -> Int,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
    val right = vstack.getFrameSlot(rightSlot).toInt()
    vstack.setFrameSlot(destinationSlot, operation(left, right).toLong())
    nextIp
}

private inline fun I32BinarySiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
    crossinline operation: (Int, Int) -> Int,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
    val left = vstack.getFrameSlot(leftSlot).toInt()
    vstack.setFrameSlot(destinationSlot, operation(left, right).toLong())
    nextIp
}

private inline fun I32BinarySsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
    crossinline operation: (Int, Int) -> Int,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
    val left = vstack.getFrameSlot(leftSlot).toInt()
    val right = vstack.getFrameSlot(rightSlot).toInt()
    vstack.setFrameSlot(destinationSlot, operation(left, right).toLong())
    nextIp
}
