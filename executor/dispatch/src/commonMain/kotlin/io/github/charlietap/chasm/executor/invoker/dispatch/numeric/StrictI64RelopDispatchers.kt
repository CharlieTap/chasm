package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64BinaryIi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64BinaryIs
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64BinarySi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeI64BinarySs

fun I64EqIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> if (left == right) 1L else 0L }
}

fun I64EqIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> if (left == right) 1L else 0L }
}

fun I64EqSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> if (left == right) 1L else 0L }
}

fun I64EqSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> if (left == right) 1L else 0L }
}

fun I64NeIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> if (left != right) 1L else 0L }
}

fun I64NeIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> if (left != right) 1L else 0L }
}

fun I64NeSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> if (left != right) 1L else 0L }
}

fun I64NeSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> if (left != right) 1L else 0L }
}

fun I64LtSIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> if (left < right) 1L else 0L }
}

fun I64LtSIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> if (left < right) 1L else 0L }
}

fun I64LtSSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> if (left < right) 1L else 0L }
}

fun I64LtSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> if (left < right) 1L else 0L }
}

fun I64LtUIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right ->
        if (left.toULong() < right.toULong()) 1L else 0L
    }
}

fun I64LtUIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        if (left.toULong() < right.toULong()) 1L else 0L
    }
}

fun I64LtUSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        if (left.toULong() < right.toULong()) 1L else 0L
    }
}

fun I64LtUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        if (left.toULong() < right.toULong()) 1L else 0L
    }
}

fun I64GtSIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> if (left > right) 1L else 0L }
}

fun I64GtSIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> if (left > right) 1L else 0L }
}

fun I64GtSSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> if (left > right) 1L else 0L }
}

fun I64GtSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> if (left > right) 1L else 0L }
}

fun I64GtUIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right ->
        if (left.toULong() > right.toULong()) 1L else 0L
    }
}

fun I64GtUIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        if (left.toULong() > right.toULong()) 1L else 0L
    }
}

fun I64GtUSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        if (left.toULong() > right.toULong()) 1L else 0L
    }
}

fun I64GtUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        if (left.toULong() > right.toULong()) 1L else 0L
    }
}

fun I64LeSIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> if (left <= right) 1L else 0L }
}

fun I64LeSIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> if (left <= right) 1L else 0L }
}

fun I64LeSSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> if (left <= right) 1L else 0L }
}

fun I64LeSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> if (left <= right) 1L else 0L }
}

fun I64LeUIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right ->
        if (left.toULong() <= right.toULong()) 1L else 0L
    }
}

fun I64LeUIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        if (left.toULong() <= right.toULong()) 1L else 0L
    }
}

fun I64LeUSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        if (left.toULong() <= right.toULong()) 1L else 0L
    }
}

fun I64LeUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        if (left.toULong() <= right.toULong()) 1L else 0L
    }
}

fun I64GeSIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right -> if (left >= right) 1L else 0L }
}

fun I64GeSIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> if (left >= right) 1L else 0L }
}

fun I64GeSSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> if (left >= right) 1L else 0L }
}

fun I64GeSSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> if (left >= right) 1L else 0L }
}

fun I64GeUIiDispatcher(
    left: Long,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIi(vstack, destinationSlot, left, right) { left, right ->
        if (left.toULong() >= right.toULong()) 1L else 0L
    }
}

fun I64GeUIsDispatcher(
    left: Long,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right ->
        if (left.toULong() >= right.toULong()) 1L else 0L
    }
}

fun I64GeUSiDispatcher(
    leftSlot: Int,
    right: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right ->
        if (left.toULong() >= right.toULong()) 1L else 0L
    }
}

fun I64GeUSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right ->
        if (left.toULong() >= right.toULong()) 1L else 0L
    }
}
