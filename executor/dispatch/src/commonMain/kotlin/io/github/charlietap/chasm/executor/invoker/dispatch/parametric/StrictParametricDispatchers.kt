package io.github.charlietap.chasm.executor.invoker.dispatch.parametric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.parametric.SelectExecutor
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction

fun SelectDispatcher(
    instruction: ParametricInstruction.SelectIii,
) = dispatchInstruction { vstack, context ->
    SelectExecutor(vstack, context, instruction)
}

fun SelectDispatcher(
    instruction: ParametricInstruction.SelectIis,
) = dispatchInstruction { vstack, context ->
    SelectExecutor(vstack, context, instruction)
}

fun SelectDispatcher(
    instruction: ParametricInstruction.SelectIsi,
) = dispatchInstruction { vstack, context ->
    SelectExecutor(vstack, context, instruction)
}

fun SelectDispatcher(
    instruction: ParametricInstruction.SelectIss,
) = dispatchInstruction { vstack, context ->
    SelectExecutor(vstack, context, instruction)
}

fun SelectDispatcher(
    instruction: ParametricInstruction.SelectSii,
) = dispatchInstruction { vstack, context ->
    SelectExecutor(vstack, context, instruction)
}

fun SelectDispatcher(
    instruction: ParametricInstruction.SelectSis,
) = dispatchInstruction { vstack, context ->
    SelectExecutor(vstack, context, instruction)
}

fun SelectDispatcher(
    instruction: ParametricInstruction.SelectSsi,
) = dispatchInstruction { vstack, context ->
    SelectExecutor(vstack, context, instruction)
}

fun SelectDispatcher(
    instruction: ParametricInstruction.SelectSss,
) = dispatchInstruction { vstack, context ->
    SelectExecutor(vstack, context, instruction)
}
