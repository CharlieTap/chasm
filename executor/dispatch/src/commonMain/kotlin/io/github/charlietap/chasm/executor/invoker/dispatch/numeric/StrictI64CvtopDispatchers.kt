package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeF32ToI64I
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeF32ToI64S
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeF64ToI64I
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeF64ToI64S
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.operations.ext.truncI64s
import io.github.charlietap.chasm.runtime.operations.ext.truncI64sTrapping
import io.github.charlietap.chasm.runtime.operations.ext.truncI64u
import io.github.charlietap.chasm.runtime.operations.ext.truncI64uTrapping

fun I64TruncF32SIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI64I(vstack, destinationSlot, operand) { operand ->
        try {
            operand.truncI64sTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I64TruncF32SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI64S(vstack, destinationSlot, operandSlot) { operand ->
        try {
            operand.truncI64sTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I64TruncF32UIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI64I(vstack, destinationSlot, operand) { operand ->
        try {
            operand.truncI64uTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I64TruncF32USDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI64S(vstack, destinationSlot, operandSlot) { operand ->
        try {
            operand.truncI64uTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I64TruncF64SIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI64I(vstack, destinationSlot, operand) { operand ->
        try {
            operand.truncI64sTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I64TruncF64SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI64S(vstack, destinationSlot, operandSlot) { operand ->
        try {
            operand.truncI64sTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I64TruncF64UIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI64I(vstack, destinationSlot, operand) { operand ->
        try {
            operand.truncI64uTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I64TruncF64USDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI64S(vstack, destinationSlot, operandSlot) { operand ->
        try {
            operand.truncI64uTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I64TruncSatF32SIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI64I(vstack, destinationSlot, operand) { operand ->
        operand.truncI64s()
    }
}

fun I64TruncSatF32SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI64S(vstack, destinationSlot, operandSlot) { operand ->
        operand.truncI64s()
    }
}

fun I64TruncSatF32UIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI64I(vstack, destinationSlot, operand) { operand ->
        operand.truncI64u()
    }
}

fun I64TruncSatF32USDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI64S(vstack, destinationSlot, operandSlot) { operand ->
        operand.truncI64u()
    }
}

fun I64TruncSatF64SIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI64I(vstack, destinationSlot, operand) { operand ->
        operand.truncI64s()
    }
}

fun I64TruncSatF64SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI64S(vstack, destinationSlot, operandSlot) { operand ->
        operand.truncI64s()
    }
}

fun I64TruncSatF64UIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI64I(vstack, destinationSlot, operand) { operand ->
        operand.truncI64u()
    }
}

fun I64TruncSatF64USDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI64S(vstack, destinationSlot, operandSlot) { operand ->
        operand.truncI64u()
    }
}
