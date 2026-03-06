package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.testop.I64EqzExecutor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64EqzDispatcher(instruction: FusedNumericInstruction.I64EqzI) = dispatchInstruction(instruction, ::I64EqzExecutor)

fun I64EqzDispatcher(instruction: FusedNumericInstruction.I64EqzS) = dispatchInstruction(instruction, ::I64EqzExecutor)
