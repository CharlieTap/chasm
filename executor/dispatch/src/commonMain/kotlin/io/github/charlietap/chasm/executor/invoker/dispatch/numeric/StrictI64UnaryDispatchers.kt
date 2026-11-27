package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeI64UnaryI
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeI64UnaryS

@InternalChasmApi
fun I64ClzIDispatcher(
    operand: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64UnaryI(vstack, destinationSlot, operand) { operand ->
        operand.countLeadingZeroBits().toLong()
    }
}

@InternalChasmApi
fun I64ClzSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64UnaryS(vstack, destinationSlot, operandSlot) { operand ->
        operand.countLeadingZeroBits().toLong()
    }
}

@InternalChasmApi
fun I64CtzIDispatcher(
    operand: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64UnaryI(vstack, destinationSlot, operand) { operand ->
        operand.countTrailingZeroBits().toLong()
    }
}

@InternalChasmApi
fun I64CtzSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64UnaryS(vstack, destinationSlot, operandSlot) { operand ->
        operand.countTrailingZeroBits().toLong()
    }
}

@InternalChasmApi
fun I64PopcntIDispatcher(
    operand: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64UnaryI(vstack, destinationSlot, operand) { operand ->
        operand.countOneBits().toLong()
    }
}

@InternalChasmApi
fun I64PopcntSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64UnaryS(vstack, destinationSlot, operandSlot) { operand ->
        operand.countOneBits().toLong()
    }
}

@InternalChasmApi
fun I64Extend8SIDispatcher(
    operand: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64UnaryI(vstack, destinationSlot, operand) { operand ->
        (operand and 0xFF).toByte().toLong()
    }
}

@InternalChasmApi
fun I64Extend8SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64UnaryS(vstack, destinationSlot, operandSlot) { operand ->
        (operand and 0xFF).toByte().toLong()
    }
}

@InternalChasmApi
fun I64Extend16SIDispatcher(
    operand: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64UnaryI(vstack, destinationSlot, operand) { operand ->
        (operand and 0xFFFF).toShort().toLong()
    }
}

@InternalChasmApi
fun I64Extend16SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64UnaryS(vstack, destinationSlot, operandSlot) { operand ->
        (operand and 0xFFFF).toShort().toLong()
    }
}

@InternalChasmApi
fun I64Extend32SIDispatcher(
    operand: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64UnaryI(vstack, destinationSlot, operand) { operand ->
        ((operand and 0xFFFFFFFFL) shl 32) shr 32
    }
}

@InternalChasmApi
fun I64Extend32SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64UnaryS(vstack, destinationSlot, operandSlot) { operand ->
        ((operand and 0xFFFFFFFFL) shl 32) shr 32
    }
}
