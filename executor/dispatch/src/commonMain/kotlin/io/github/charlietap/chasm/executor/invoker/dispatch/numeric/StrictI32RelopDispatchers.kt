package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI32BinaryIi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI32BinaryIs
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI32BinarySi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI32BinarySs

fun I32EqIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right ->
        if (left == right) 1 else 0
    }
}

fun I32EqIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        if (left == right) 1 else 0
    }
}

fun I32EqSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        if (left == right) 1 else 0
    }
}

fun I32EqSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        if (left == right) 1 else 0
    }
}

fun I32NeIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right ->
        if (left != right) 1 else 0
    }
}

fun I32NeIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        if (left != right) 1 else 0
    }
}

fun I32NeSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        if (left != right) 1 else 0
    }
}

fun I32NeSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        if (left != right) 1 else 0
    }
}

fun I32LtSIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right ->
        if (left < right) 1 else 0
    }
}

fun I32LtSIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        if (left < right) 1 else 0
    }
}

fun I32LtSSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        if (left < right) 1 else 0
    }
}

fun I32LtSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        if (left < right) 1 else 0
    }
}

fun I32LtUIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right ->
        if (left.toUInt() < right.toUInt()) 1 else 0
    }
}

fun I32LtUIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        if (left.toUInt() < right.toUInt()) 1 else 0
    }
}

fun I32LtUSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        if (left.toUInt() < right.toUInt()) 1 else 0
    }
}

fun I32LtUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        if (left.toUInt() < right.toUInt()) 1 else 0
    }
}

fun I32GtSIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right ->
        if (left > right) 1 else 0
    }
}

fun I32GtSIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        if (left > right) 1 else 0
    }
}

fun I32GtSSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        if (left > right) 1 else 0
    }
}

fun I32GtSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        if (left > right) 1 else 0
    }
}

fun I32GtUIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right ->
        if (left.toUInt() > right.toUInt()) 1 else 0
    }
}

fun I32GtUIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        if (left.toUInt() > right.toUInt()) 1 else 0
    }
}

fun I32GtUSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        if (left.toUInt() > right.toUInt()) 1 else 0
    }
}

fun I32GtUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        if (left.toUInt() > right.toUInt()) 1 else 0
    }
}

fun I32LeSIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right ->
        if (left <= right) 1 else 0
    }
}

fun I32LeSIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        if (left <= right) 1 else 0
    }
}

fun I32LeSSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        if (left <= right) 1 else 0
    }
}

fun I32LeSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        if (left <= right) 1 else 0
    }
}

fun I32LeUIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right ->
        if (left.toUInt() <= right.toUInt()) 1 else 0
    }
}

fun I32LeUIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        if (left.toUInt() <= right.toUInt()) 1 else 0
    }
}

fun I32LeUSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        if (left.toUInt() <= right.toUInt()) 1 else 0
    }
}

fun I32LeUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        if (left.toUInt() <= right.toUInt()) 1 else 0
    }
}

fun I32GeSIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right ->
        if (left >= right) 1 else 0
    }
}

fun I32GeSIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        if (left >= right) 1 else 0
    }
}

fun I32GeSSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        if (left >= right) 1 else 0
    }
}

fun I32GeSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        if (left >= right) 1 else 0
    }
}

fun I32GeUIiDispatcher(
    left: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIi(vstack, destinationSlot, left, right) { left, right ->
        if (left.toUInt() >= right.toUInt()) 1 else 0
    }
}

fun I32GeUIsDispatcher(
    left: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        if (left.toUInt() >= right.toUInt()) 1 else 0
    }
}

fun I32GeUSiDispatcher(
    leftSlot: Int,
    right: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        if (left.toUInt() >= right.toUInt()) 1 else 0
    }
}

fun I32GeUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        if (left.toUInt() >= right.toUInt()) 1 else 0
    }
}
