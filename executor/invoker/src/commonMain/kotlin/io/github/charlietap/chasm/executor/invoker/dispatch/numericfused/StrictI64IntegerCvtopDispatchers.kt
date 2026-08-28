package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64ExtendI32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64ExtendI32UExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

fun I64ExtendI32SDispatcher(instruction: NumericSuperInstruction.I64ExtendI32SI) = dispatchInstruction { vstack, context -> I64ExtendI32SExecutor(vstack, context, instruction) }

fun I64ExtendI32SDispatcher(instruction: NumericSuperInstruction.I64ExtendI32SS) = dispatchInstruction { vstack, context -> I64ExtendI32SExecutor(vstack, context, instruction) }

fun I64ExtendI32UDispatcher(instruction: NumericSuperInstruction.I64ExtendI32UI) = dispatchInstruction { vstack, context -> I64ExtendI32UExecutor(vstack, context, instruction) }

fun I64ExtendI32UDispatcher(instruction: NumericSuperInstruction.I64ExtendI32US) = dispatchInstruction { vstack, context -> I64ExtendI32UExecutor(vstack, context, instruction) }
