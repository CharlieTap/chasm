package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI32BinaryIi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI32BinaryIs
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI32BinarySi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI32BinarySs
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.operations.strictI32DivS
import io.github.charlietap.chasm.runtime.operations.strictI32DivU
import io.github.charlietap.chasm.runtime.operations.strictI32RemS
import io.github.charlietap.chasm.runtime.operations.strictI32RemU

@InternalChasmApi
fun I32AddIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = I32BinaryIsDispatcher(
    left,
    rightSlot,
    destinationSlot,
) { left, right -> left + right }

@InternalChasmApi
fun I32AddSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = I32BinarySiDispatcher(
    leftSlot,
    right,
    destinationSlot,
) { left, right -> left + right }

@InternalChasmApi
fun I32AddSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = I32BinarySsDispatcher(
    leftSlot,
    rightSlot,
    destinationSlot,
) { left, right -> left + right }

@InternalChasmApi
fun I32SubIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = I32BinaryIiDispatcher(
    left,
    right,
    destinationSlot,
) { left, right -> left - right }

@InternalChasmApi
fun I32SubIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = I32BinaryIsDispatcher(
    left,
    rightSlot,
    destinationSlot,
) { left, right -> left - right }

@InternalChasmApi
fun I32SubSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = I32BinarySiDispatcher(
    leftSlot,
    right,
    destinationSlot,
) { left, right -> left - right }

@InternalChasmApi
fun I32SubSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = I32BinarySsDispatcher(
    leftSlot,
    rightSlot,
    destinationSlot,
) { left, right -> left - right }

@InternalChasmApi
fun I32MulIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = I32BinaryIiDispatcher(
    left,
    right,
    destinationSlot,
) { left, right -> left * right }

@InternalChasmApi
fun I32MulIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = I32BinaryIsDispatcher(
    left,
    rightSlot,
    destinationSlot,
) { left, right -> left * right }

@InternalChasmApi
fun I32MulSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = I32BinarySiDispatcher(
    leftSlot,
    right,
    destinationSlot,
) { left, right -> left * right }

@InternalChasmApi
fun I32MulSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = I32BinarySsDispatcher(
    leftSlot,
    rightSlot,
    destinationSlot,
) { left, right -> left * right }

@InternalChasmApi
fun I32DivSIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right, ::strictI32DivS)
}

@InternalChasmApi
fun I32DivSIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot, ::strictI32DivS)
}

@InternalChasmApi
fun I32DivSSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right, ::strictI32DivS)
}

@InternalChasmApi
fun I32DivSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot, ::strictI32DivS)
}

@InternalChasmApi
fun I32DivUIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right, ::strictI32DivU)
}

@InternalChasmApi
fun I32DivUIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot, ::strictI32DivU)
}

@InternalChasmApi
fun I32DivUSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right, ::strictI32DivU)
}

@InternalChasmApi
fun I32DivUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot, ::strictI32DivU)
}

@InternalChasmApi
fun I32RemSIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right, ::strictI32RemS)
}

@InternalChasmApi
fun I32RemSIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot, ::strictI32RemS)
}

@InternalChasmApi
fun I32RemSSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right, ::strictI32RemS)
}

@InternalChasmApi
fun I32RemSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot, ::strictI32RemS)
}

@InternalChasmApi
fun I32RemUIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right, ::strictI32RemU)
}

@InternalChasmApi
fun I32RemUIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot, ::strictI32RemU)
}

@InternalChasmApi
fun I32RemUSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right, ::strictI32RemU)
}

@InternalChasmApi
fun I32RemUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot, ::strictI32RemU)
}

@InternalChasmApi
fun I32AndIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = I32BinaryIiDispatcher(
    left,
    right,
    destinationSlot,
) { left, right -> left and right }

@InternalChasmApi
fun I32AndIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = I32BinaryIsDispatcher(
    left,
    rightSlot,
    destinationSlot,
) { left, right -> left and right }

@InternalChasmApi
fun I32AndSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = I32BinarySiDispatcher(
    leftSlot,
    right,
    destinationSlot,
) { left, right -> left and right }

@InternalChasmApi
fun I32AndSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = I32BinarySsDispatcher(
    leftSlot,
    rightSlot,
    destinationSlot,
) { left, right -> left and right }

@InternalChasmApi
fun I32OrIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = I32BinaryIiDispatcher(
    left,
    right,
    destinationSlot,
) { left, right -> left or right }

@InternalChasmApi
fun I32OrIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = I32BinaryIsDispatcher(
    left,
    rightSlot,
    destinationSlot,
) { left, right -> left or right }

@InternalChasmApi
fun I32OrSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = I32BinarySiDispatcher(
    leftSlot,
    right,
    destinationSlot,
) { left, right -> left or right }

@InternalChasmApi
fun I32OrSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = I32BinarySsDispatcher(
    leftSlot,
    rightSlot,
    destinationSlot,
) { left, right -> left or right }

@InternalChasmApi
fun I32XorIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = I32BinaryIiDispatcher(
    left,
    right,
    destinationSlot,
) { left, right -> left xor right }

@InternalChasmApi
fun I32XorIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = I32BinaryIsDispatcher(
    left,
    rightSlot,
    destinationSlot,
) { left, right -> left xor right }

@InternalChasmApi
fun I32XorSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = I32BinarySiDispatcher(
    leftSlot,
    right,
    destinationSlot,
) { left, right -> left xor right }

@InternalChasmApi
fun I32XorSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = I32BinarySsDispatcher(
    leftSlot,
    rightSlot,
    destinationSlot,
) { left, right -> left xor right }

@InternalChasmApi
fun I32ShlIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right -> left shl right }
}

@InternalChasmApi
fun I32ShlIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left shl right }
}

@InternalChasmApi
fun I32ShlSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
): DispatchableInstruction {
    return DispatchableInstruction { vstack, _, nextIp ->
        val value = vstack.getFrameSlot(leftSlot).toInt() shl right
        vstack.setFrameSlot(destinationSlot, value.toLong())
        nextIp
    }
}

@InternalChasmApi
fun I32ShlSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left shl right }
}

@InternalChasmApi
fun I32ShrSIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right -> left shr right }
}

@InternalChasmApi
fun I32ShrSIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left shr right }
}

@InternalChasmApi
fun I32ShrSSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
): DispatchableInstruction {
    return DispatchableInstruction { vstack, _, nextIp ->
        val value = vstack.getFrameSlot(leftSlot).toInt() shr right
        vstack.setFrameSlot(destinationSlot, value.toLong())
        nextIp
    }
}

@InternalChasmApi
fun I32ShrSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left shr right }
}

@InternalChasmApi
fun I32ShrUIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right -> left.toUInt().shr(right).toInt() }
}

@InternalChasmApi
fun I32ShrUIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left.toUInt().shr(right).toInt() }
}

@InternalChasmApi
fun I32ShrUSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
): DispatchableInstruction {
    return DispatchableInstruction { vstack, _, nextIp ->
        val value = vstack.getFrameSlot(leftSlot).toInt() ushr right
        vstack.setFrameSlot(destinationSlot, value.toLong())
        nextIp
    }
}

@InternalChasmApi
fun I32ShrUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left.toUInt().shr(right).toInt() }
}

@InternalChasmApi
fun I32RotlIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right -> left.rotateLeft(right) }
}

@InternalChasmApi
fun I32RotlIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left.rotateLeft(right) }
}

@InternalChasmApi
fun I32RotlSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left.rotateLeft(right) }
}

@InternalChasmApi
fun I32RotlSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left.rotateLeft(right) }
}

@InternalChasmApi
fun I32RotrIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right -> left.rotateRight(right) }
}

@InternalChasmApi
fun I32RotrIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left.rotateRight(right) }
}

@InternalChasmApi
fun I32RotrSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left.rotateRight(right) }
}

@InternalChasmApi
fun I32RotrSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left.rotateRight(right) }
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
