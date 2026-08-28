package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop.I64EqzExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64EqzDispatcher(
    instruction: NumericInstruction.I64EqzI,
) = dispatchInstruction { vstack, context ->
    I64EqzExecutor(vstack, context, instruction)
}

fun I64EqzDispatcher(
    instruction: NumericInstruction.I64EqzS,
) = dispatchInstruction { vstack, context ->
    I64EqzExecutor(vstack, context, instruction)
}
