package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncF32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncF32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncF64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncF64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32WrapI64Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32TruncF32SDispatcher(
    instruction: NumericInstruction.I32TruncF32SI,
) = dispatchInstruction { vstack, context -> I32TruncF32SExecutor(vstack, context, instruction) }

fun I32TruncF32SDispatcher(
    instruction: NumericInstruction.I32TruncF32SS,
) = dispatchInstruction { vstack, context -> I32TruncF32SExecutor(vstack, context, instruction) }

fun I32TruncF32UDispatcher(
    instruction: NumericInstruction.I32TruncF32UI,
) = dispatchInstruction { vstack, context -> I32TruncF32UExecutor(vstack, context, instruction) }

fun I32TruncF32UDispatcher(
    instruction: NumericInstruction.I32TruncF32US,
) = dispatchInstruction { vstack, context -> I32TruncF32UExecutor(vstack, context, instruction) }

fun I32TruncF64SDispatcher(
    instruction: NumericInstruction.I32TruncF64SI,
) = dispatchInstruction { vstack, context -> I32TruncF64SExecutor(vstack, context, instruction) }

fun I32TruncF64SDispatcher(
    instruction: NumericInstruction.I32TruncF64SS,
) = dispatchInstruction { vstack, context -> I32TruncF64SExecutor(vstack, context, instruction) }

fun I32TruncF64UDispatcher(
    instruction: NumericInstruction.I32TruncF64UI,
) = dispatchInstruction { vstack, context ->
    I32TruncF64UExecutor(vstack, context, instruction)
}

fun I32TruncF64UDispatcher(
    instruction: NumericInstruction.I32TruncF64US,
) = dispatchInstruction { vstack, context -> I32TruncF64UExecutor(vstack, context, instruction) }

fun I32TruncSatF32SDispatcher(
    instruction: NumericInstruction.I32TruncSatF32SI,
) = dispatchInstruction { vstack, context -> I32TruncSatF32SExecutor(vstack, context, instruction) }

fun I32TruncSatF32SDispatcher(
    instruction: NumericInstruction.I32TruncSatF32SS,
) = dispatchInstruction { vstack, context -> I32TruncSatF32SExecutor(vstack, context, instruction) }

fun I32TruncSatF32UDispatcher(
    instruction: NumericInstruction.I32TruncSatF32UI,
) = dispatchInstruction { vstack, context -> I32TruncSatF32UExecutor(vstack, context, instruction) }

fun I32TruncSatF32UDispatcher(
    instruction: NumericInstruction.I32TruncSatF32US,
) = dispatchInstruction { vstack, context -> I32TruncSatF32UExecutor(vstack, context, instruction) }

fun I32TruncSatF64SDispatcher(
    instruction: NumericInstruction.I32TruncSatF64SI,
) = dispatchInstruction { vstack, context -> I32TruncSatF64SExecutor(vstack, context, instruction) }

fun I32TruncSatF64SDispatcher(
    instruction: NumericInstruction.I32TruncSatF64SS,
) = dispatchInstruction { vstack, context -> I32TruncSatF64SExecutor(vstack, context, instruction) }

fun I32TruncSatF64UDispatcher(
    instruction: NumericInstruction.I32TruncSatF64UI,
) = dispatchInstruction { vstack, context -> I32TruncSatF64UExecutor(vstack, context, instruction) }

fun I32TruncSatF64UDispatcher(
    instruction: NumericInstruction.I32TruncSatF64US,
) = dispatchInstruction { vstack, context -> I32TruncSatF64UExecutor(vstack, context, instruction) }

fun I32WrapI64Dispatcher(
    instruction: NumericInstruction.I32WrapI64I,
) = dispatchInstruction { vstack, context -> I32WrapI64Executor(vstack, context, instruction) }

fun I32WrapI64Dispatcher(
    instruction: NumericInstruction.I32WrapI64S,
) = dispatchInstruction { vstack, context -> I32WrapI64Executor(vstack, context, instruction) }
