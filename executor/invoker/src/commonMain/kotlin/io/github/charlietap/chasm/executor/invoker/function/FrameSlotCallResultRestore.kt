package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.stack.NO_RESULT_SLOT_BASE
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun FinishFrameSlotCallResult(
    vstack: ValueStack,
    resultArity: Int,
    valueDepth: Int,
    callerFramePointer: Int,
    resultSlotBase: Int,
) {
    val currentFramePointer = vstack.framePointer
    if (resultArity == 0) {
        vstack.framePointer = callerFramePointer
        vstack.shrink(
            preserveTopN = 0,
            depth = valueDepth,
        )
        return
    }

    if (resultSlotBase != NO_RESULT_SLOT_BASE) {
        val resultsAlreadyInPlace = currentFramePointer == callerFramePointer + resultSlotBase

        if (resultsAlreadyInPlace) {
            vstack.framePointer = callerFramePointer
            vstack.shrink(
                preserveTopN = 0,
                depth = valueDepth,
            )
            return
        }

        vstack.copyFrameSlots(
            sourceFramePointer = currentFramePointer,
            destinationFramePointer = callerFramePointer,
            sourceSlot = 0,
            destinationSlot = resultSlotBase,
            count = resultArity,
        )
        vstack.framePointer = callerFramePointer
        vstack.shrink(
            preserveTopN = 0,
            depth = valueDepth,
        )
        return
    }

    vstack.shrinkFromFrameSlots(
        slot = 0,
        count = resultArity,
        depth = valueDepth,
    )
    vstack.framePointer = callerFramePointer
}
