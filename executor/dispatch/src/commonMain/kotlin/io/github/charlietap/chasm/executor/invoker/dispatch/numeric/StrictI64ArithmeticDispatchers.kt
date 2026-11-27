package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64BinaryIi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64BinaryIs
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64BinarySi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64BinarySs
import io.github.charlietap.chasm.runtime.operations.strictI64DivS
import io.github.charlietap.chasm.runtime.operations.strictI64DivU
import io.github.charlietap.chasm.runtime.operations.strictI64RemS
import io.github.charlietap.chasm.runtime.operations.strictI64RemU

@InternalChasmApi
fun I64AddIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left + right }
}

@InternalChasmApi
fun I64AddIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left + right }
}

@InternalChasmApi
fun I64AddSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left + right }
}

@InternalChasmApi
fun I64AddSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left + right }
}

@InternalChasmApi
fun I64SubIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left - right }
}

@InternalChasmApi
fun I64SubIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left - right }
}

@InternalChasmApi
fun I64SubSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left - right }
}

@InternalChasmApi
fun I64SubSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left - right }
}

@InternalChasmApi
fun I64MulIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left * right }
}

@InternalChasmApi
fun I64MulIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left * right }
}

@InternalChasmApi
fun I64MulSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left * right }
}

@InternalChasmApi
fun I64MulSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left * right }
}

@InternalChasmApi
fun I64DivSIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right, ::strictI64DivS)
}

@InternalChasmApi
fun I64DivSIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot, ::strictI64DivS)
}

@InternalChasmApi
fun I64DivSSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right, ::strictI64DivS)
}

@InternalChasmApi
fun I64DivSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot, ::strictI64DivS)
}

@InternalChasmApi
fun I64DivUIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right, ::strictI64DivU)
}

@InternalChasmApi
fun I64DivUIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot, ::strictI64DivU)
}

@InternalChasmApi
fun I64DivUSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right, ::strictI64DivU)
}

@InternalChasmApi
fun I64DivUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot, ::strictI64DivU)
}

@InternalChasmApi
fun I64RemSIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right, ::strictI64RemS)
}

@InternalChasmApi
fun I64RemSIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot, ::strictI64RemS)
}

@InternalChasmApi
fun I64RemSSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right, ::strictI64RemS)
}

@InternalChasmApi
fun I64RemSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot, ::strictI64RemS)
}

@InternalChasmApi
fun I64RemUIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right, ::strictI64RemU)
}

@InternalChasmApi
fun I64RemUIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot, ::strictI64RemU)
}

@InternalChasmApi
fun I64RemUSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right, ::strictI64RemU)
}

@InternalChasmApi
fun I64RemUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot, ::strictI64RemU)
}

@InternalChasmApi
fun I64AndIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left and right }
}

@InternalChasmApi
fun I64AndIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left and right }
}

@InternalChasmApi
fun I64AndSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left and right }
}

@InternalChasmApi
fun I64AndSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left and right }
}

@InternalChasmApi
fun I64OrIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left or right }
}

@InternalChasmApi
fun I64OrIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left or right }
}

@InternalChasmApi
fun I64OrSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left or right }
}

@InternalChasmApi
fun I64OrSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left or right }
}

@InternalChasmApi
fun I64XorIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left xor right }
}

@InternalChasmApi
fun I64XorIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left xor right }
}

@InternalChasmApi
fun I64XorSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left xor right }
}

@InternalChasmApi
fun I64XorSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left xor right }
}

@InternalChasmApi
fun I64ShlIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left shl right.toInt() }
}

@InternalChasmApi
fun I64ShlIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left shl right.toInt() }
}

@InternalChasmApi
fun I64ShlSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left shl right.toInt() }
}

@InternalChasmApi
fun I64ShlSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left shl right.toInt() }
}

@InternalChasmApi
fun I64ShrSIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left shr right.toInt() }
}

@InternalChasmApi
fun I64ShrSIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left shr right.toInt() }
}

@InternalChasmApi
fun I64ShrSSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left shr right.toInt() }
}

@InternalChasmApi
fun I64ShrSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left shr right.toInt() }
}

@InternalChasmApi
fun I64ShrUIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left.toULong().shr(right.toInt()).toLong() }
}

@InternalChasmApi
fun I64ShrUIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        left.toULong().shr(right.toInt()).toLong()
    }
}

@InternalChasmApi
fun I64ShrUSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        left.toULong().shr(right.toInt()).toLong()
    }
}

@InternalChasmApi
fun I64ShrUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        left.toULong().shr(right.toInt()).toLong()
    }
}

@InternalChasmApi
fun I64RotlIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left.rotateLeft(right.toInt()) }
}

@InternalChasmApi
fun I64RotlIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left.rotateLeft(right.toInt()) }
}

@InternalChasmApi
fun I64RotlSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left.rotateLeft(right.toInt()) }
}

@InternalChasmApi
fun I64RotlSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left.rotateLeft(right.toInt()) }
}

@InternalChasmApi
fun I64RotrIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left.rotateRight(right.toInt()) }
}

@InternalChasmApi
fun I64RotrIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left.rotateRight(right.toInt()) }
}

@InternalChasmApi
fun I64RotrSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left.rotateRight(right.toInt()) }
}

@InternalChasmApi
fun I64RotrSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left.rotateRight(right.toInt()) }
}
