package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeI32UnaryI
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeI32UnaryS

@InternalChasmApi
fun I32ClzIDispatcher(
    operand: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32UnaryI(vstack, destinationSlot, operand) { operand -> operand.countLeadingZeroBits() }
}

@InternalChasmApi
fun I32ClzSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32UnaryS(vstack, destinationSlot, operandSlot) { operand -> operand.countLeadingZeroBits() }
}

@InternalChasmApi
fun I32CtzIDispatcher(
    operand: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32UnaryI(vstack, destinationSlot, operand) { operand -> operand.countTrailingZeroBits() }
}

@InternalChasmApi
fun I32CtzSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32UnaryS(vstack, destinationSlot, operandSlot) { operand -> operand.countTrailingZeroBits() }
}

@InternalChasmApi
fun I32PopcntIDispatcher(
    operand: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32UnaryI(vstack, destinationSlot, operand) { operand -> operand.countOneBits() }
}

@InternalChasmApi
fun I32PopcntSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32UnaryS(vstack, destinationSlot, operandSlot) { operand -> operand.countOneBits() }
}

@InternalChasmApi
fun I32Extend8SIDispatcher(
    operand: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32UnaryI(vstack, destinationSlot, operand) { operand -> (operand and 0xFF).toByte().toInt() }
}

@InternalChasmApi
fun I32Extend8SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32UnaryS(vstack, destinationSlot, operandSlot) { operand -> (operand and 0xFF).toByte().toInt() }
}

@InternalChasmApi
fun I32Extend16SIDispatcher(
    operand: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32UnaryI(vstack, destinationSlot, operand) { operand -> (operand and 0xFFFF).toShort().toInt() }
}

@InternalChasmApi
fun I32Extend16SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32UnaryS(vstack, destinationSlot, operandSlot) { operand -> (operand and 0xFFFF).toShort().toInt() }
}
