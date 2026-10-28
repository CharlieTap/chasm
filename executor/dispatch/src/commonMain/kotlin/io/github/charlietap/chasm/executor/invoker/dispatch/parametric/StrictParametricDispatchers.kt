package io.github.charlietap.chasm.executor.invoker.dispatch.parametric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.parametric.executeSelect

fun SelectIiiDispatcher(
    condition: Long,
    val1: Long,
    val2: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeSelect(
        vstack = vstack,
        condition = condition,
        val1 = val1,
        val2 = val2,
        destinationSlot = destinationSlot,
    )
}

fun SelectIisDispatcher(
    condition: Long,
    val1: Long,
    val2Slot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeSelect(
        vstack = vstack,
        condition = condition,
        val1 = val1,
        val2 = vstack.getFrameSlot(val2Slot),
        destinationSlot = destinationSlot,
    )
}

fun SelectIsiDispatcher(
    condition: Long,
    val1Slot: Int,
    val2: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeSelect(
        vstack = vstack,
        condition = condition,
        val1 = vstack.getFrameSlot(val1Slot),
        val2 = val2,
        destinationSlot = destinationSlot,
    )
}

fun SelectIssDispatcher(
    condition: Long,
    val1Slot: Int,
    val2Slot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeSelect(
        vstack = vstack,
        condition = condition,
        val1 = vstack.getFrameSlot(val1Slot),
        val2 = vstack.getFrameSlot(val2Slot),
        destinationSlot = destinationSlot,
    )
}

fun SelectSiiDispatcher(
    conditionSlot: Int,
    val1: Long,
    val2: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeSelect(
        vstack = vstack,
        condition = vstack.getFrameSlot(conditionSlot),
        val1 = val1,
        val2 = val2,
        destinationSlot = destinationSlot,
    )
}

fun SelectSisDispatcher(
    conditionSlot: Int,
    val1: Long,
    val2Slot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeSelect(
        vstack = vstack,
        condition = vstack.getFrameSlot(conditionSlot),
        val1 = val1,
        val2 = vstack.getFrameSlot(val2Slot),
        destinationSlot = destinationSlot,
    )
}

fun SelectSsiDispatcher(
    conditionSlot: Int,
    val1Slot: Int,
    val2: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeSelect(
        vstack = vstack,
        condition = vstack.getFrameSlot(conditionSlot),
        val1 = vstack.getFrameSlot(val1Slot),
        val2 = val2,
        destinationSlot = destinationSlot,
    )
}

fun SelectSssDispatcher(
    conditionSlot: Int,
    val1Slot: Int,
    val2Slot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeSelect(
        vstack = vstack,
        condition = vstack.getFrameSlot(conditionSlot),
        val1 = vstack.getFrameSlot(val1Slot),
        val2 = vstack.getFrameSlot(val2Slot),
        destinationSlot = destinationSlot,
    )
}
