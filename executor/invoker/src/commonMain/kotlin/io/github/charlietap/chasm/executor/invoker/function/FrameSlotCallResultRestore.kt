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

        val resultValues = LongArray(resultArity) { index ->
            vstack.getFrameSlot(index)
        }

        vstack.framePointer = callerFramePointer
        resultValues.forEachIndexed { index, value ->
            vstack.setFrameSlot(resultSlotBase + index, value)
        }
        vstack.shrink(
            preserveTopN = 0,
            depth = valueDepth,
        )
        return
    }

    vstack.shrinkFromFrameSlots(
        slots = List(resultArity, ::identity),
        depth = valueDepth,
    )
    vstack.framePointer = callerFramePointer
}

private fun identity(index: Int): Int = index
