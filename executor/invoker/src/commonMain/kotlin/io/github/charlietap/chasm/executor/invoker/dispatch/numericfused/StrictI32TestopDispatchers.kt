package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.testop.I32EqzExecutor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32EqzDispatcher(instruction: FusedNumericInstruction.I32EqzI) = dispatchInstruction(instruction, ::I32EqzExecutor)

fun I32EqzDispatcher(instruction: FusedNumericInstruction.I32EqzS) = dispatchInstruction(instruction, ::I32EqzExecutor)
