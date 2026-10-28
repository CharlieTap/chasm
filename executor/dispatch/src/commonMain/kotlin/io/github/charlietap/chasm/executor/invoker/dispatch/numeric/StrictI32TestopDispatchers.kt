package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop.I32EqzExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32EqzDispatcher(
    instruction: NumericInstruction.I32EqzI,
) = dispatchInstruction { vstack, context ->
    I32EqzExecutor(vstack, context, instruction)
}

fun I32EqzDispatcher(
    instruction: NumericInstruction.I32EqzS,
) = dispatchInstruction { vstack, context ->
    I32EqzExecutor(vstack, context, instruction)
}
