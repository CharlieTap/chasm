package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeI32UnaryI
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeI32UnaryS

fun I32EqzIDispatcher(
    operand: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32UnaryI(vstack, destinationSlot, operand) { operand ->
        if (operand == 0) 1 else 0
    }
}

fun I32EqzSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32UnaryS(vstack, destinationSlot, operandSlot) { operand ->
        if (operand == 0) 1 else 0
    }
}
