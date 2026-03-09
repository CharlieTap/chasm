package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32ReinterpretF32Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncF32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncF32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncF64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncF64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncSatF32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncSatF32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncSatF64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncSatF64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32WrapI64Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32ReinterpretF32Dispatcher(
    instruction: FusedNumericInstruction.I32ReinterpretF32I,
) = dispatchInstruction(instruction, ::I32ReinterpretF32Executor)

fun I32ReinterpretF32Dispatcher(
    instruction: FusedNumericInstruction.I32ReinterpretF32S,
) = dispatchInstruction(instruction, ::I32ReinterpretF32Executor)

fun I32TruncF32SDispatcher(
    instruction: FusedNumericInstruction.I32TruncF32SI,
) = dispatchInstruction(instruction, ::I32TruncF32SExecutor)

fun I32TruncF32SDispatcher(
    instruction: FusedNumericInstruction.I32TruncF32SS,
) = dispatchInstruction(instruction, ::I32TruncF32SExecutor)

fun I32TruncF32UDispatcher(
    instruction: FusedNumericInstruction.I32TruncF32UI,
) = dispatchInstruction(instruction, ::I32TruncF32UExecutor)

fun I32TruncF32UDispatcher(
    instruction: FusedNumericInstruction.I32TruncF32US,
) = dispatchInstruction(instruction, ::I32TruncF32UExecutor)

fun I32TruncF64SDispatcher(
    instruction: FusedNumericInstruction.I32TruncF64SI,
) = dispatchInstruction(instruction, ::I32TruncF64SExecutor)

fun I32TruncF64SDispatcher(
    instruction: FusedNumericInstruction.I32TruncF64SS,
) = dispatchInstruction(instruction, ::I32TruncF64SExecutor)

fun I32TruncF64UDispatcher(
    instruction: FusedNumericInstruction.I32TruncF64UI,
) = dispatchInstruction(instruction, ::I32TruncF64UExecutor)

fun I32TruncF64UDispatcher(
    instruction: FusedNumericInstruction.I32TruncF64US,
) = dispatchInstruction(instruction, ::I32TruncF64UExecutor)

fun I32TruncSatF32SDispatcher(
    instruction: FusedNumericInstruction.I32TruncSatF32SI,
) = dispatchInstruction(instruction, ::I32TruncSatF32SExecutor)

fun I32TruncSatF32SDispatcher(
    instruction: FusedNumericInstruction.I32TruncSatF32SS,
) = dispatchInstruction(instruction, ::I32TruncSatF32SExecutor)

fun I32TruncSatF32UDispatcher(
    instruction: FusedNumericInstruction.I32TruncSatF32UI,
) = dispatchInstruction(instruction, ::I32TruncSatF32UExecutor)

fun I32TruncSatF32UDispatcher(
    instruction: FusedNumericInstruction.I32TruncSatF32US,
) = dispatchInstruction(instruction, ::I32TruncSatF32UExecutor)

fun I32TruncSatF64SDispatcher(
    instruction: FusedNumericInstruction.I32TruncSatF64SI,
) = dispatchInstruction(instruction, ::I32TruncSatF64SExecutor)

fun I32TruncSatF64SDispatcher(
    instruction: FusedNumericInstruction.I32TruncSatF64SS,
) = dispatchInstruction(instruction, ::I32TruncSatF64SExecutor)

fun I32TruncSatF64UDispatcher(
    instruction: FusedNumericInstruction.I32TruncSatF64UI,
) = dispatchInstruction(instruction, ::I32TruncSatF64UExecutor)

fun I32TruncSatF64UDispatcher(
    instruction: FusedNumericInstruction.I32TruncSatF64US,
) = dispatchInstruction(instruction, ::I32TruncSatF64UExecutor)

fun I32WrapI64Dispatcher(
    instruction: FusedNumericInstruction.I32WrapI64I,
) = dispatchInstruction(instruction, ::I32WrapI64Executor)

fun I32WrapI64Dispatcher(
    instruction: FusedNumericInstruction.I32WrapI64S,
) = dispatchInstruction(instruction, ::I32WrapI64Executor)
