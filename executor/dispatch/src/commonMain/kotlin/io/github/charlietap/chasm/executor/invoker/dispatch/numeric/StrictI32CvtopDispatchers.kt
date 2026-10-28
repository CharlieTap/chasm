package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeF32ToI32I
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeF32ToI32S
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeF64ToI32I
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeF64ToI32S
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeI64ToI32I
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeI64ToI32S
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.operations.ext.truncI32s
import io.github.charlietap.chasm.runtime.operations.ext.truncI32sTrapping
import io.github.charlietap.chasm.runtime.operations.ext.truncI32u
import io.github.charlietap.chasm.runtime.operations.ext.truncI32uTrapping
import io.github.charlietap.chasm.runtime.operations.ext.wrap

fun I32TruncF32SIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI32I(vstack, destinationSlot, operand) { operand ->
        try {
            operand.truncI32sTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I32TruncF32SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI32S(vstack, destinationSlot, operandSlot) { operand ->
        try {
            operand.truncI32sTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I32TruncF32UIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI32I(vstack, destinationSlot, operand) { operand ->
        try {
            operand.truncI32uTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I32TruncF32USDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI32S(vstack, destinationSlot, operandSlot) { operand ->
        try {
            operand.truncI32uTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I32TruncF64SIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI32I(vstack, destinationSlot, operand) { operand ->
        try {
            operand.truncI32sTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I32TruncF64SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI32S(vstack, destinationSlot, operandSlot) { operand ->
        try {
            operand.truncI32sTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I32TruncF64UIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI32I(vstack, destinationSlot, operand) { operand ->
        try {
            operand.truncI32uTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I32TruncF64USDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI32S(vstack, destinationSlot, operandSlot) { operand ->
        try {
            operand.truncI32uTrapping()
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ConvertOperationFailed)
        }
    }
}

fun I32TruncSatF32SIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI32I(vstack, destinationSlot, operand) { operand ->
        operand.truncI32s()
    }
}

fun I32TruncSatF32SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI32S(vstack, destinationSlot, operandSlot) { operand ->
        operand.truncI32s()
    }
}

fun I32TruncSatF32UIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI32I(vstack, destinationSlot, operand) { operand ->
        operand.truncI32u()
    }
}

fun I32TruncSatF32USDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToI32S(vstack, destinationSlot, operandSlot) { operand ->
        operand.truncI32u()
    }
}

fun I32TruncSatF64SIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI32I(vstack, destinationSlot, operand) { operand ->
        operand.truncI32s()
    }
}

fun I32TruncSatF64SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI32S(vstack, destinationSlot, operandSlot) { operand ->
        operand.truncI32s()
    }
}

fun I32TruncSatF64UIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI32I(vstack, destinationSlot, operand) { operand ->
        operand.truncI32u()
    }
}

fun I32TruncSatF64USDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToI32S(vstack, destinationSlot, operandSlot) { operand ->
        operand.truncI32u()
    }
}

fun I32WrapI64IDispatcher(
    operand: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64ToI32I(vstack, destinationSlot, operand) { operand ->
        operand.wrap()
    }
}

fun I32WrapI64SDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64ToI32S(vstack, destinationSlot, operandSlot) { operand ->
        operand.wrap()
    }
}
