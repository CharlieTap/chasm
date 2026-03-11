package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.testop.I32EqzExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

fun I32EqzDispatcher(instruction: NumericSuperInstruction.I32EqzI) = dispatchInstruction(instruction, ::I32EqzExecutor)

fun I32EqzDispatcher(instruction: NumericSuperInstruction.I32EqzS) = dispatchInstruction(instruction, ::I32EqzExecutor)
