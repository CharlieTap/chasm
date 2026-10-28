package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64Add128
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64MulWideSigned
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64MulWideUnsigned
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64Sub128

fun I64Add128IiiiDispatcher(
    leftLow: Long,
    leftHigh: Long,
    rightLow: Long,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        leftHigh,
        rightLow,
        rightHigh,
    )
}

fun I64Add128IiisDispatcher(
    leftLow: Long,
    leftHigh: Long,
    rightLow: Long,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        leftHigh,
        rightLow,
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Add128IisiDispatcher(
    leftLow: Long,
    leftHigh: Long,
    rightLowSlot: Int,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        leftHigh,
        vstack.getFrameSlot(rightLowSlot),
        rightHigh,
    )
}

fun I64Add128IissDispatcher(
    leftLow: Long,
    leftHigh: Long,
    rightLowSlot: Int,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        leftHigh,
        vstack.getFrameSlot(rightLowSlot),
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Add128IsiiDispatcher(
    leftLow: Long,
    leftHighSlot: Int,
    rightLow: Long,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        vstack.getFrameSlot(leftHighSlot),
        rightLow,
        rightHigh,
    )
}

fun I64Add128IsisDispatcher(
    leftLow: Long,
    leftHighSlot: Int,
    rightLow: Long,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        vstack.getFrameSlot(leftHighSlot),
        rightLow,
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Add128IssiDispatcher(
    leftLow: Long,
    leftHighSlot: Int,
    rightLowSlot: Int,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        vstack.getFrameSlot(leftHighSlot),
        vstack.getFrameSlot(rightLowSlot),
        rightHigh,
    )
}

fun I64Add128IsssDispatcher(
    leftLow: Long,
    leftHighSlot: Int,
    rightLowSlot: Int,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        vstack.getFrameSlot(leftHighSlot),
        vstack.getFrameSlot(rightLowSlot),
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Add128SiiiDispatcher(
    leftLowSlot: Int,
    leftHigh: Long,
    rightLow: Long,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        leftHigh,
        rightLow,
        rightHigh,
    )
}

fun I64Add128SiisDispatcher(
    leftLowSlot: Int,
    leftHigh: Long,
    rightLow: Long,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        leftHigh,
        rightLow,
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Add128SisiDispatcher(
    leftLowSlot: Int,
    leftHigh: Long,
    rightLowSlot: Int,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        leftHigh,
        vstack.getFrameSlot(rightLowSlot),
        rightHigh,
    )
}

fun I64Add128SissDispatcher(
    leftLowSlot: Int,
    leftHigh: Long,
    rightLowSlot: Int,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        leftHigh,
        vstack.getFrameSlot(rightLowSlot),
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Add128SsiiDispatcher(
    leftLowSlot: Int,
    leftHighSlot: Int,
    rightLow: Long,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        vstack.getFrameSlot(leftHighSlot),
        rightLow,
        rightHigh,
    )
}

fun I64Add128SsisDispatcher(
    leftLowSlot: Int,
    leftHighSlot: Int,
    rightLow: Long,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        vstack.getFrameSlot(leftHighSlot),
        rightLow,
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Add128SssiDispatcher(
    leftLowSlot: Int,
    leftHighSlot: Int,
    rightLowSlot: Int,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        vstack.getFrameSlot(leftHighSlot),
        vstack.getFrameSlot(rightLowSlot),
        rightHigh,
    )
}

fun I64Add128SsssDispatcher(
    leftLowSlot: Int,
    leftHighSlot: Int,
    rightLowSlot: Int,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Add128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        vstack.getFrameSlot(leftHighSlot),
        vstack.getFrameSlot(rightLowSlot),
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Sub128IiiiDispatcher(
    leftLow: Long,
    leftHigh: Long,
    rightLow: Long,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        leftHigh,
        rightLow,
        rightHigh,
    )
}

fun I64Sub128IiisDispatcher(
    leftLow: Long,
    leftHigh: Long,
    rightLow: Long,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        leftHigh,
        rightLow,
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Sub128IisiDispatcher(
    leftLow: Long,
    leftHigh: Long,
    rightLowSlot: Int,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        leftHigh,
        vstack.getFrameSlot(rightLowSlot),
        rightHigh,
    )
}

fun I64Sub128IissDispatcher(
    leftLow: Long,
    leftHigh: Long,
    rightLowSlot: Int,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        leftHigh,
        vstack.getFrameSlot(rightLowSlot),
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Sub128IsiiDispatcher(
    leftLow: Long,
    leftHighSlot: Int,
    rightLow: Long,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        vstack.getFrameSlot(leftHighSlot),
        rightLow,
        rightHigh,
    )
}

fun I64Sub128IsisDispatcher(
    leftLow: Long,
    leftHighSlot: Int,
    rightLow: Long,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        vstack.getFrameSlot(leftHighSlot),
        rightLow,
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Sub128IssiDispatcher(
    leftLow: Long,
    leftHighSlot: Int,
    rightLowSlot: Int,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        vstack.getFrameSlot(leftHighSlot),
        vstack.getFrameSlot(rightLowSlot),
        rightHigh,
    )
}

fun I64Sub128IsssDispatcher(
    leftLow: Long,
    leftHighSlot: Int,
    rightLowSlot: Int,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        leftLow,
        vstack.getFrameSlot(leftHighSlot),
        vstack.getFrameSlot(rightLowSlot),
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Sub128SiiiDispatcher(
    leftLowSlot: Int,
    leftHigh: Long,
    rightLow: Long,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        leftHigh,
        rightLow,
        rightHigh,
    )
}

fun I64Sub128SiisDispatcher(
    leftLowSlot: Int,
    leftHigh: Long,
    rightLow: Long,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        leftHigh,
        rightLow,
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Sub128SisiDispatcher(
    leftLowSlot: Int,
    leftHigh: Long,
    rightLowSlot: Int,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        leftHigh,
        vstack.getFrameSlot(rightLowSlot),
        rightHigh,
    )
}

fun I64Sub128SissDispatcher(
    leftLowSlot: Int,
    leftHigh: Long,
    rightLowSlot: Int,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        leftHigh,
        vstack.getFrameSlot(rightLowSlot),
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Sub128SsiiDispatcher(
    leftLowSlot: Int,
    leftHighSlot: Int,
    rightLow: Long,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        vstack.getFrameSlot(leftHighSlot),
        rightLow,
        rightHigh,
    )
}

fun I64Sub128SsisDispatcher(
    leftLowSlot: Int,
    leftHighSlot: Int,
    rightLow: Long,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        vstack.getFrameSlot(leftHighSlot),
        rightLow,
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64Sub128SssiDispatcher(
    leftLowSlot: Int,
    leftHighSlot: Int,
    rightLowSlot: Int,
    rightHigh: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        vstack.getFrameSlot(leftHighSlot),
        vstack.getFrameSlot(rightLowSlot),
        rightHigh,
    )
}

fun I64Sub128SsssDispatcher(
    leftLowSlot: Int,
    leftHighSlot: Int,
    rightLowSlot: Int,
    rightHighSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64Sub128(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftLowSlot),
        vstack.getFrameSlot(leftHighSlot),
        vstack.getFrameSlot(rightLowSlot),
        vstack.getFrameSlot(rightHighSlot),
    )
}

fun I64MulWideSIiDispatcher(
    left: Long,
    right: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64MulWideSigned(vstack, destinationLowSlot, destinationHighSlot, left, right)
}

fun I64MulWideSIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64MulWideSigned(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        left,
        vstack.getFrameSlot(rightSlot),
    )
}

fun I64MulWideSSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64MulWideSigned(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftSlot),
        right,
    )
}

fun I64MulWideSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64MulWideSigned(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftSlot),
        vstack.getFrameSlot(rightSlot),
    )
}

fun I64MulWideUIiDispatcher(
    left: Long,
    right: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64MulWideUnsigned(vstack, destinationLowSlot, destinationHighSlot, left, right)
}

fun I64MulWideUIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64MulWideUnsigned(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        left,
        vstack.getFrameSlot(rightSlot),
    )
}

fun I64MulWideUSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64MulWideUnsigned(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftSlot),
        right,
    )
}

fun I64MulWideUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64MulWideUnsigned(
        vstack,
        destinationLowSlot,
        destinationHighSlot,
        vstack.getFrameSlot(leftSlot),
        vstack.getFrameSlot(rightSlot),
    )
}
