package io.github.charlietap.chasm.executor.invoker.dispatch.variable

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.variable.GlobalGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.variable.GlobalSetExecutor
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

fun GlobalGetDispatcher(
    instruction: VariableInstruction.GlobalGetS,
) = dispatchInstruction { vstack, context ->
    GlobalGetExecutor(vstack, context, instruction)
}

fun GlobalSetDispatcher(
    instruction: VariableInstruction.GlobalSetI,
) = dispatchInstruction { vstack, context ->
    GlobalSetExecutor(vstack, context, instruction)
}

fun GlobalSetDispatcher(
    instruction: VariableInstruction.GlobalSetS,
) = dispatchInstruction { vstack, context ->
    GlobalSetExecutor(vstack, context, instruction)
}
