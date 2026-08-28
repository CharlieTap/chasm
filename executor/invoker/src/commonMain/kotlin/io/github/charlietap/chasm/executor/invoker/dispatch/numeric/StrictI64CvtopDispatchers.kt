package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF64UExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64TruncF32SDispatcher(
    instruction: NumericInstruction.I64TruncF32SI,
) = dispatchInstruction { vstack, context ->
    I64TruncF32SExecutor(vstack, context, instruction)
}

fun I64TruncF32SDispatcher(
    instruction: NumericInstruction.I64TruncF32SS,
) = dispatchInstruction { vstack, context ->
    I64TruncF32SExecutor(vstack, context, instruction)
}

fun I64TruncF32UDispatcher(
    instruction: NumericInstruction.I64TruncF32UI,
) = dispatchInstruction { vstack, context ->
    I64TruncF32UExecutor(vstack, context, instruction)
}

fun I64TruncF32UDispatcher(
    instruction: NumericInstruction.I64TruncF32US,
) = dispatchInstruction { vstack, context ->
    I64TruncF32UExecutor(vstack, context, instruction)
}

fun I64TruncF64SDispatcher(
    instruction: NumericInstruction.I64TruncF64SI,
) = dispatchInstruction { vstack, context ->
    I64TruncF64SExecutor(vstack, context, instruction)
}

fun I64TruncF64SDispatcher(
    instruction: NumericInstruction.I64TruncF64SS,
) = dispatchInstruction { vstack, context ->
    I64TruncF64SExecutor(vstack, context, instruction)
}

fun I64TruncF64UDispatcher(
    instruction: NumericInstruction.I64TruncF64UI,
) = dispatchInstruction { vstack, context ->
    I64TruncF64UExecutor(vstack, context, instruction)
}

fun I64TruncF64UDispatcher(
    instruction: NumericInstruction.I64TruncF64US,
) = dispatchInstruction { vstack, context ->
    I64TruncF64UExecutor(vstack, context, instruction)
}

fun I64TruncSatF32SDispatcher(
    instruction: NumericInstruction.I64TruncSatF32SI,
) = dispatchInstruction { vstack, context ->
    I64TruncSatF32SExecutor(vstack, context, instruction)
}

fun I64TruncSatF32SDispatcher(
    instruction: NumericInstruction.I64TruncSatF32SS,
) = dispatchInstruction { vstack, context ->
    I64TruncSatF32SExecutor(vstack, context, instruction)
}

fun I64TruncSatF32UDispatcher(
    instruction: NumericInstruction.I64TruncSatF32UI,
) = dispatchInstruction { vstack, context ->
    I64TruncSatF32UExecutor(vstack, context, instruction)
}

fun I64TruncSatF32UDispatcher(
    instruction: NumericInstruction.I64TruncSatF32US,
) = dispatchInstruction { vstack, context ->
    I64TruncSatF32UExecutor(vstack, context, instruction)
}

fun I64TruncSatF64SDispatcher(
    instruction: NumericInstruction.I64TruncSatF64SI,
) = dispatchInstruction { vstack, context ->
    I64TruncSatF64SExecutor(vstack, context, instruction)
}

fun I64TruncSatF64SDispatcher(
    instruction: NumericInstruction.I64TruncSatF64SS,
) = dispatchInstruction { vstack, context ->
    I64TruncSatF64SExecutor(vstack, context, instruction)
}

fun I64TruncSatF64UDispatcher(
    instruction: NumericInstruction.I64TruncSatF64UI,
) = dispatchInstruction { vstack, context ->
    I64TruncSatF64UExecutor(vstack, context, instruction)
}

fun I64TruncSatF64UDispatcher(
    instruction: NumericInstruction.I64TruncSatF64US,
) = dispatchInstruction { vstack, context ->
    I64TruncSatF64UExecutor(vstack, context, instruction)
}
