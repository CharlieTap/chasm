package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.ValueStack

// Legacy stack-mode functions still return through the value stack directly.
internal fun RestoreLegacyCallResult(
    vstack: ValueStack,
    frame: ActivationFrame,
) {
    if (frame.frameSlotMode) return

    vstack.shrink(
        preserveTopN = frame.arity,
        depth = frame.depths.values,
    )
}

internal fun FinishStrictFrameSlotCallResult(
    vstack: ValueStack,
    frame: ActivationFrame,
): Boolean {
    if (!frame.frameSlotMode) return false

    val currentFramePointer = vstack.framePointer
    val callerFramePointer = frame.previousFramePointer
    val resultArity = frame.arity
    if (resultArity == 0) {
        vstack.framePointer = callerFramePointer
        vstack.shrink(
            preserveTopN = 0,
            depth = frame.depths.values,
        )
        return true
    }

    val visibleResultBase = FrameSlotVisibleResultBase(frame)

    if (visibleResultBase != null) {
        val sharesVisibleResultRegion = currentFramePointer == callerFramePointer + visibleResultBase

        if (sharesVisibleResultRegion) {
            vstack.framePointer = callerFramePointer
            vstack.shrink(
                preserveTopN = 0,
                depth = frame.depths.values,
            )
            return true
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
        return true
    }

    vstack.shrinkFromFrameSlots(
        slots = List(resultArity, ::identity),
        depth = frame.depths.values,
    )
    vstack.framePointer = callerFramePointer
    return true
}

internal fun FrameSlotVisibleResultBase(
    frame: ActivationFrame,
): Int? = frame.visibleResultBase
    ?.takeIf { frame.frameSlotMode }

internal fun StrictVisibleResultBase(
    resultSlots: List<Int>,
): Int = resultSlots.firstOrNull() ?: 0

private fun identity(index: Int): Int = index
