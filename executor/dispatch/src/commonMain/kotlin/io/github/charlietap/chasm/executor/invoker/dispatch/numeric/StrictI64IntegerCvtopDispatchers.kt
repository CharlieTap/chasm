package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction

@InternalChasmApi
fun I64ExtendI32SIDispatcher(
    operand: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    run {
        vstack.setFrameSlot(destinationSlot, operand.toLong())
    }
}

@InternalChasmApi
fun I64ExtendI32SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    run {
        vstack.setFrameSlot(destinationSlot, vstack.getFrameSlot(operandSlot).toInt().toLong())
    }
}

@InternalChasmApi
fun I64ExtendI32UIDispatcher(
    operand: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    run {
        vstack.setFrameSlot(destinationSlot, operand.toUInt().toLong())
    }
}

@InternalChasmApi
fun I64ExtendI32USDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    run {
        vstack.setFrameSlot(destinationSlot, vstack.getFrameSlot(operandSlot).toInt().toUInt().toLong())
    }
}
