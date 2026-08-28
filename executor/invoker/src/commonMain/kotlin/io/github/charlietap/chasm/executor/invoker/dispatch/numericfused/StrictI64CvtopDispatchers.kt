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

fun I64ReinterpretF64Dispatcher(instruction: NumericSuperInstruction.I64ReinterpretF64I) = dispatchInstruction { vstack, context -> I64ReinterpretF64Executor(vstack, context, instruction) }

fun I64ReinterpretF64Dispatcher(instruction: NumericSuperInstruction.I64ReinterpretF64S) = dispatchInstruction { vstack, context -> I64ReinterpretF64Executor(vstack, context, instruction) }

fun I64TruncF32SDispatcher(instruction: NumericSuperInstruction.I64TruncF32SI) = dispatchInstruction { vstack, context -> I64TruncF32SExecutor(vstack, context, instruction) }

fun I64TruncF32SDispatcher(instruction: NumericSuperInstruction.I64TruncF32SS) = dispatchInstruction { vstack, context -> I64TruncF32SExecutor(vstack, context, instruction) }

fun I64TruncF32UDispatcher(instruction: NumericSuperInstruction.I64TruncF32UI) = dispatchInstruction { vstack, context -> I64TruncF32UExecutor(vstack, context, instruction) }

fun I64TruncF32UDispatcher(instruction: NumericSuperInstruction.I64TruncF32US) = dispatchInstruction { vstack, context -> I64TruncF32UExecutor(vstack, context, instruction) }

fun I64TruncF64SDispatcher(instruction: NumericSuperInstruction.I64TruncF64SI) = dispatchInstruction { vstack, context -> I64TruncF64SExecutor(vstack, context, instruction) }

fun I64TruncF64SDispatcher(instruction: NumericSuperInstruction.I64TruncF64SS) = dispatchInstruction { vstack, context -> I64TruncF64SExecutor(vstack, context, instruction) }

fun I64TruncF64UDispatcher(instruction: NumericSuperInstruction.I64TruncF64UI) = dispatchInstruction { vstack, context -> I64TruncF64UExecutor(vstack, context, instruction) }

fun I64TruncF64UDispatcher(instruction: NumericSuperInstruction.I64TruncF64US) = dispatchInstruction { vstack, context -> I64TruncF64UExecutor(vstack, context, instruction) }

fun I64TruncSatF32SDispatcher(instruction: NumericSuperInstruction.I64TruncSatF32SI) = dispatchInstruction { vstack, context -> I64TruncSatF32SExecutor(vstack, context, instruction) }

fun I64TruncSatF32SDispatcher(instruction: NumericSuperInstruction.I64TruncSatF32SS) = dispatchInstruction { vstack, context -> I64TruncSatF32SExecutor(vstack, context, instruction) }

fun I64TruncSatF32UDispatcher(instruction: NumericSuperInstruction.I64TruncSatF32UI) = dispatchInstruction { vstack, context -> I64TruncSatF32UExecutor(vstack, context, instruction) }

fun I64TruncSatF32UDispatcher(instruction: NumericSuperInstruction.I64TruncSatF32US) = dispatchInstruction { vstack, context -> I64TruncSatF32UExecutor(vstack, context, instruction) }

fun I64TruncSatF64SDispatcher(instruction: NumericSuperInstruction.I64TruncSatF64SI) = dispatchInstruction { vstack, context -> I64TruncSatF64SExecutor(vstack, context, instruction) }

fun I64TruncSatF64SDispatcher(instruction: NumericSuperInstruction.I64TruncSatF64SS) = dispatchInstruction { vstack, context -> I64TruncSatF64SExecutor(vstack, context, instruction) }

fun I64TruncSatF64UDispatcher(instruction: NumericSuperInstruction.I64TruncSatF64UI) = dispatchInstruction { vstack, context -> I64TruncSatF64UExecutor(vstack, context, instruction) }

fun I64TruncSatF64UDispatcher(instruction: NumericSuperInstruction.I64TruncSatF64US) = dispatchInstruction { vstack, context -> I64TruncSatF64UExecutor(vstack, context, instruction) }
