package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeI64UnaryI
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeI64UnaryS

fun I64EqzIDispatcher(
    operand: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64UnaryI(vstack, destinationSlot, operand) { operand ->
        if (operand == 0L) 1L else 0L
    }
}

fun I64EqzSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64UnaryS(vstack, destinationSlot, operandSlot) { operand ->
        if (operand == 0L) 1L else 0L
    }
}
