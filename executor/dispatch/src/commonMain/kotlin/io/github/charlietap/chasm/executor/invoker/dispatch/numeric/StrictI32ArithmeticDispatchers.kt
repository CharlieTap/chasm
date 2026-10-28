package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32DivSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32DivUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RemSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RemUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RotlExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RotrExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32ShlExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32ShrSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32ShrUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32AddDispatcher(instruction: NumericInstruction.I32AddIs) = I32BinaryIsDispatcher(
    instruction.left,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left + right }

fun I32AddDispatcher(instruction: NumericInstruction.I32AddSi) = I32BinarySiDispatcher(
    instruction.leftSlot,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left + right }

fun I32AddDispatcher(instruction: NumericInstruction.I32AddSs) = I32BinarySsDispatcher(
    instruction.leftSlot,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left + right }

fun I32SubDispatcher(instruction: NumericInstruction.I32SubIi) = I32BinaryIiDispatcher(
    instruction.left,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left - right }

fun I32SubDispatcher(instruction: NumericInstruction.I32SubIs) = I32BinaryIsDispatcher(
    instruction.left,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left - right }

fun I32SubDispatcher(instruction: NumericInstruction.I32SubSi) = I32BinarySiDispatcher(
    instruction.leftSlot,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left - right }

fun I32SubDispatcher(instruction: NumericInstruction.I32SubSs) = I32BinarySsDispatcher(
    instruction.leftSlot,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left - right }

fun I32MulDispatcher(instruction: NumericInstruction.I32MulIi) = I32BinaryIiDispatcher(
    instruction.left,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left * right }

fun I32MulDispatcher(instruction: NumericInstruction.I32MulIs) = I32BinaryIsDispatcher(
    instruction.left,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left * right }

fun I32MulDispatcher(instruction: NumericInstruction.I32MulSi) = I32BinarySiDispatcher(
    instruction.leftSlot,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left * right }

fun I32MulDispatcher(instruction: NumericInstruction.I32MulSs) = I32BinarySsDispatcher(
    instruction.leftSlot,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left * right }

fun I32DivSDispatcher(
    instruction: NumericInstruction.I32DivSIi,
) = dispatchInstruction { vstack, context ->
    I32DivSExecutor(vstack, context, instruction)
}

fun I32DivSDispatcher(
    instruction: NumericInstruction.I32DivSIs,
) = dispatchInstruction { vstack, context ->
    I32DivSExecutor(vstack, context, instruction)
}

fun I32DivSDispatcher(
    instruction: NumericInstruction.I32DivSSi,
) = dispatchInstruction { vstack, context ->
    I32DivSExecutor(vstack, context, instruction)
}

fun I32DivSDispatcher(
    instruction: NumericInstruction.I32DivSSs,
) = dispatchInstruction { vstack, context ->
    I32DivSExecutor(vstack, context, instruction)
}

fun I32DivUDispatcher(
    instruction: NumericInstruction.I32DivUIi,
) = dispatchInstruction { vstack, context ->
    I32DivUExecutor(vstack, context, instruction)
}

fun I32DivUDispatcher(
    instruction: NumericInstruction.I32DivUIs,
) = dispatchInstruction { vstack, context ->
    I32DivUExecutor(vstack, context, instruction)
}

fun I32DivUDispatcher(
    instruction: NumericInstruction.I32DivUSi,
) = dispatchInstruction { vstack, context ->
    I32DivUExecutor(vstack, context, instruction)
}

fun I32DivUDispatcher(
    instruction: NumericInstruction.I32DivUSs,
) = dispatchInstruction { vstack, context ->
    I32DivUExecutor(vstack, context, instruction)
}

fun I32RemSDispatcher(
    instruction: NumericInstruction.I32RemSIi,
) = dispatchInstruction { vstack, context ->
    I32RemSExecutor(vstack, context, instruction)
}

fun I32RemSDispatcher(
    instruction: NumericInstruction.I32RemSIs,
) = dispatchInstruction { vstack, context ->
    I32RemSExecutor(vstack, context, instruction)
}

fun I32RemSDispatcher(
    instruction: NumericInstruction.I32RemSSi,
) = dispatchInstruction { vstack, context ->
    I32RemSExecutor(vstack, context, instruction)
}

fun I32RemSDispatcher(
    instruction: NumericInstruction.I32RemSSs,
) = dispatchInstruction { vstack, context ->
    I32RemSExecutor(vstack, context, instruction)
}

fun I32RemUDispatcher(
    instruction: NumericInstruction.I32RemUIi,
) = dispatchInstruction { vstack, context ->
    I32RemUExecutor(vstack, context, instruction)
}

fun I32RemUDispatcher(
    instruction: NumericInstruction.I32RemUIs,
) = dispatchInstruction { vstack, context ->
    I32RemUExecutor(vstack, context, instruction)
}

fun I32RemUDispatcher(
    instruction: NumericInstruction.I32RemUSi,
) = dispatchInstruction { vstack, context ->
    I32RemUExecutor(vstack, context, instruction)
}

fun I32RemUDispatcher(
    instruction: NumericInstruction.I32RemUSs,
) = dispatchInstruction { vstack, context ->
    I32RemUExecutor(vstack, context, instruction)
}

fun I32AndDispatcher(instruction: NumericInstruction.I32AndIi) = I32BinaryIiDispatcher(
    instruction.left,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left and right }

fun I32AndDispatcher(instruction: NumericInstruction.I32AndIs) = I32BinaryIsDispatcher(
    instruction.left,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left and right }

fun I32AndDispatcher(instruction: NumericInstruction.I32AndSi) = I32BinarySiDispatcher(
    instruction.leftSlot,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left and right }

fun I32AndDispatcher(instruction: NumericInstruction.I32AndSs) = I32BinarySsDispatcher(
    instruction.leftSlot,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left and right }

fun I32OrDispatcher(instruction: NumericInstruction.I32OrIi) = I32BinaryIiDispatcher(
    instruction.left,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left or right }

fun I32OrDispatcher(instruction: NumericInstruction.I32OrIs) = I32BinaryIsDispatcher(
    instruction.left,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left or right }

fun I32OrDispatcher(instruction: NumericInstruction.I32OrSi) = I32BinarySiDispatcher(
    instruction.leftSlot,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left or right }

fun I32OrDispatcher(instruction: NumericInstruction.I32OrSs) = I32BinarySsDispatcher(
    instruction.leftSlot,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left or right }

fun I32XorDispatcher(instruction: NumericInstruction.I32XorIi) = I32BinaryIiDispatcher(
    instruction.left,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left xor right }

fun I32XorDispatcher(instruction: NumericInstruction.I32XorIs) = I32BinaryIsDispatcher(
    instruction.left,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left xor right }

fun I32XorDispatcher(instruction: NumericInstruction.I32XorSi) = I32BinarySiDispatcher(
    instruction.leftSlot,
    instruction.right,
    instruction.destinationSlot,
) { left, right -> left xor right }

fun I32XorDispatcher(instruction: NumericInstruction.I32XorSs) = I32BinarySsDispatcher(
    instruction.leftSlot,
    instruction.rightSlot,
    instruction.destinationSlot,
) { left, right -> left xor right }

fun I32ShlDispatcher(
    instruction: NumericInstruction.I32ShlIi,
) = dispatchInstruction { vstack, context ->
    I32ShlExecutor(vstack, context, instruction)
}

fun I32ShlDispatcher(
    instruction: NumericInstruction.I32ShlIs,
) = dispatchInstruction { vstack, context ->
    I32ShlExecutor(vstack, context, instruction)
}

fun I32ShlDispatcher(
    instruction: NumericInstruction.I32ShlSi,
): DispatchableInstruction {
    val leftSlot = instruction.leftSlot
    val right = instruction.right
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, nextIp ->
        val value = vstack.getFrameSlot(leftSlot).toInt() shl right
        vstack.setFrameSlot(destinationSlot, value.toLong())
        nextIp
    }
}

fun I32ShlDispatcher(
    instruction: NumericInstruction.I32ShlSs,
) = dispatchInstruction { vstack, context ->
    I32ShlExecutor(vstack, context, instruction)
}

fun I32ShrSDispatcher(
    instruction: NumericInstruction.I32ShrSIi,
) = dispatchInstruction { vstack, context ->
    I32ShrSExecutor(vstack, context, instruction)
}

fun I32ShrSDispatcher(
    instruction: NumericInstruction.I32ShrSIs,
) = dispatchInstruction { vstack, context ->
    I32ShrSExecutor(vstack, context, instruction)
}

fun I32ShrSDispatcher(
    instruction: NumericInstruction.I32ShrSSi,
): DispatchableInstruction {
    val leftSlot = instruction.leftSlot
    val right = instruction.right
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, nextIp ->
        val value = vstack.getFrameSlot(leftSlot).toInt() shr right
        vstack.setFrameSlot(destinationSlot, value.toLong())
        nextIp
    }
}

fun I32ShrSDispatcher(
    instruction: NumericInstruction.I32ShrSSs,
) = dispatchInstruction { vstack, context ->
    I32ShrSExecutor(vstack, context, instruction)
}

fun I32ShrUDispatcher(
    instruction: NumericInstruction.I32ShrUIi,
) = dispatchInstruction { vstack, context ->
    I32ShrUExecutor(vstack, context, instruction)
}

fun I32ShrUDispatcher(
    instruction: NumericInstruction.I32ShrUIs,
) = dispatchInstruction { vstack, context ->
    I32ShrUExecutor(vstack, context, instruction)
}

fun I32ShrUDispatcher(
    instruction: NumericInstruction.I32ShrUSi,
): DispatchableInstruction {
    val leftSlot = instruction.leftSlot
    val right = instruction.right
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, nextIp ->
        val value = vstack.getFrameSlot(leftSlot).toInt() ushr right
        vstack.setFrameSlot(destinationSlot, value.toLong())
        nextIp
    }
}

fun I32ShrUDispatcher(
    instruction: NumericInstruction.I32ShrUSs,
) = dispatchInstruction { vstack, context ->
    I32ShrUExecutor(vstack, context, instruction)
}

fun I32RotlDispatcher(
    instruction: NumericInstruction.I32RotlIi,
) = dispatchInstruction { vstack, context ->
    I32RotlExecutor(vstack, context, instruction)
}

fun I32RotlDispatcher(
    instruction: NumericInstruction.I32RotlIs,
) = dispatchInstruction { vstack, context ->
    I32RotlExecutor(vstack, context, instruction)
}

fun I32RotlDispatcher(
    instruction: NumericInstruction.I32RotlSi,
) = dispatchInstruction { vstack, context ->
    I32RotlExecutor(vstack, context, instruction)
}

fun I32RotlDispatcher(
    instruction: NumericInstruction.I32RotlSs,
) = dispatchInstruction { vstack, context ->
    I32RotlExecutor(vstack, context, instruction)
}

fun I32RotrDispatcher(
    instruction: NumericInstruction.I32RotrIi,
) = dispatchInstruction { vstack, context ->
    I32RotrExecutor(vstack, context, instruction)
}

fun I32RotrDispatcher(
    instruction: NumericInstruction.I32RotrIs,
) = dispatchInstruction { vstack, context ->
    I32RotrExecutor(vstack, context, instruction)
}

fun I32RotrDispatcher(
    instruction: NumericInstruction.I32RotrSi,
) = dispatchInstruction { vstack, context ->
    I32RotrExecutor(vstack, context, instruction)
}

fun I32RotrDispatcher(
    instruction: NumericInstruction.I32RotrSs,
) = dispatchInstruction { vstack, context ->
    I32RotrExecutor(vstack, context, instruction)
}

private inline fun I32BinaryIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
    crossinline operation: (Int, Int) -> Int,
): DispatchableInstruction = DispatchableInstruction { vstack, _, nextIp ->
    vstack.setFrameSlot(destinationSlot, operation(left, right).toLong())
    nextIp
}

private inline fun I32BinaryIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
    crossinline operation: (Int, Int) -> Int,
): DispatchableInstruction = DispatchableInstruction { vstack, _, nextIp ->
    val right = vstack.getFrameSlot(rightSlot).toInt()
    vstack.setFrameSlot(destinationSlot, operation(left, right).toLong())
    nextIp
}

private inline fun I32BinarySiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
    crossinline operation: (Int, Int) -> Int,
): DispatchableInstruction = DispatchableInstruction { vstack, _, nextIp ->
    val left = vstack.getFrameSlot(leftSlot).toInt()
    vstack.setFrameSlot(destinationSlot, operation(left, right).toLong())
    nextIp
}

private inline fun I32BinarySsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
    crossinline operation: (Int, Int) -> Int,
): DispatchableInstruction = DispatchableInstruction { vstack, _, nextIp ->
    val left = vstack.getFrameSlot(leftSlot).toInt()
    val right = vstack.getFrameSlot(rightSlot).toInt()
    vstack.setFrameSlot(destinationSlot, operation(left, right).toLong())
    nextIp
}
