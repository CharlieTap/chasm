package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun FinishFrameSlotCallResult(
    vstack: ValueStack,
    frame: ActivationFrame,
) {
    val currentFramePointer = vstack.framePointer
    val callerFramePointer = frame.previousFramePointer
    val resultArity = frame.arity
    if (resultArity == 0) {
        vstack.framePointer = callerFramePointer
        vstack.shrink(
            preserveTopN = 0,
            depth = frame.depths.values,
        )
        return
    }

    val visibleResultBase = frame.visibleResultBase

    if (visibleResultBase != null) {
        val sharesVisibleResultRegion = currentFramePointer == callerFramePointer + visibleResultBase

        if (sharesVisibleResultRegion) {
            vstack.framePointer = callerFramePointer
            vstack.shrink(
                preserveTopN = 0,
                depth = frame.depths.values,
            )
            return
        }

        val resultValues = LongArray(resultArity) { index ->
            vstack.getFrameSlot(index)
        }

        vstack.framePointer = callerFramePointer
        resultValues.forEachIndexed { index, value ->
            vstack.setFrameSlot(visibleResultBase + index, value)
        }
        vstack.shrink(
            preserveTopN = 0,
            depth = frame.depths.values,
        )
        return
    }

    vstack.shrinkFromFrameSlots(
        slots = List(resultArity, ::identity),
        depth = frame.depths.values,
    )
    vstack.framePointer = callerFramePointer
}

internal fun StrictVisibleResultBase(
    resultSlots: List<Int>,
): Int = resultSlots.firstOrNull() ?: 0

private fun identity(index: Int): Int = index
