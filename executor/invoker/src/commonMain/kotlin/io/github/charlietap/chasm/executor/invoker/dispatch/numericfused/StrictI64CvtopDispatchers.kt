package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64ReinterpretF64Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncF32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncF32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncF64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncF64UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncSatF32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncSatF32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncSatF64SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncSatF64UExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

fun I64ReinterpretF64Dispatcher(instruction: NumericSuperInstruction.I64ReinterpretF64I) = dispatchInstruction(instruction, ::I64ReinterpretF64Executor)

fun I64ReinterpretF64Dispatcher(instruction: NumericSuperInstruction.I64ReinterpretF64S) = dispatchInstruction(instruction, ::I64ReinterpretF64Executor)

fun I64TruncF32SDispatcher(instruction: NumericSuperInstruction.I64TruncF32SI) = dispatchInstruction(instruction, ::I64TruncF32SExecutor)

fun I64TruncF32SDispatcher(instruction: NumericSuperInstruction.I64TruncF32SS) = dispatchInstruction(instruction, ::I64TruncF32SExecutor)

fun I64TruncF32UDispatcher(instruction: NumericSuperInstruction.I64TruncF32UI) = dispatchInstruction(instruction, ::I64TruncF32UExecutor)

fun I64TruncF32UDispatcher(instruction: NumericSuperInstruction.I64TruncF32US) = dispatchInstruction(instruction, ::I64TruncF32UExecutor)

fun I64TruncF64SDispatcher(instruction: NumericSuperInstruction.I64TruncF64SI) = dispatchInstruction(instruction, ::I64TruncF64SExecutor)

fun I64TruncF64SDispatcher(instruction: NumericSuperInstruction.I64TruncF64SS) = dispatchInstruction(instruction, ::I64TruncF64SExecutor)

fun I64TruncF64UDispatcher(instruction: NumericSuperInstruction.I64TruncF64UI) = dispatchInstruction(instruction, ::I64TruncF64UExecutor)

fun I64TruncF64UDispatcher(instruction: NumericSuperInstruction.I64TruncF64US) = dispatchInstruction(instruction, ::I64TruncF64UExecutor)

fun I64TruncSatF32SDispatcher(instruction: NumericSuperInstruction.I64TruncSatF32SI) = dispatchInstruction(instruction, ::I64TruncSatF32SExecutor)

fun I64TruncSatF32SDispatcher(instruction: NumericSuperInstruction.I64TruncSatF32SS) = dispatchInstruction(instruction, ::I64TruncSatF32SExecutor)

fun I64TruncSatF32UDispatcher(instruction: NumericSuperInstruction.I64TruncSatF32UI) = dispatchInstruction(instruction, ::I64TruncSatF32UExecutor)

fun I64TruncSatF32UDispatcher(instruction: NumericSuperInstruction.I64TruncSatF32US) = dispatchInstruction(instruction, ::I64TruncSatF32UExecutor)

fun I64TruncSatF64SDispatcher(instruction: NumericSuperInstruction.I64TruncSatF64SI) = dispatchInstruction(instruction, ::I64TruncSatF64SExecutor)

fun I64TruncSatF64SDispatcher(instruction: NumericSuperInstruction.I64TruncSatF64SS) = dispatchInstruction(instruction, ::I64TruncSatF64SExecutor)

fun I64TruncSatF64UDispatcher(instruction: NumericSuperInstruction.I64TruncSatF64UI) = dispatchInstruction(instruction, ::I64TruncSatF64UExecutor)

fun I64TruncSatF64UDispatcher(instruction: NumericSuperInstruction.I64TruncSatF64US) = dispatchInstruction(instruction, ::I64TruncSatF64UExecutor)
