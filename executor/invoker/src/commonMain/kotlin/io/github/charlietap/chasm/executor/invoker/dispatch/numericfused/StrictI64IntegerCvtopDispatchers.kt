package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64ExtendI32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64ExtendI32UExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

fun I64ExtendI32SDispatcher(instruction: NumericSuperInstruction.I64ExtendI32SI) = dispatchInstruction(instruction, ::I64ExtendI32SExecutor)

fun I64ExtendI32SDispatcher(instruction: NumericSuperInstruction.I64ExtendI32SS) = dispatchInstruction(instruction, ::I64ExtendI32SExecutor)

fun I64ExtendI32UDispatcher(instruction: NumericSuperInstruction.I64ExtendI32UI) = dispatchInstruction(instruction, ::I64ExtendI32UExecutor)

fun I64ExtendI32UDispatcher(instruction: NumericSuperInstruction.I64ExtendI32US) = dispatchInstruction(instruction, ::I64ExtendI32UExecutor)
