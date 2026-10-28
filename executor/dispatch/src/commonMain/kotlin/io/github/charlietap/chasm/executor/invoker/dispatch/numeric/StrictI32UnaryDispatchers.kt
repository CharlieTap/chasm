package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32ClzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32CtzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32Extend16SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32Extend8SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32PopcntExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32ClzDispatcher(
    instruction: NumericInstruction.I32ClzI,
) = dispatchInstruction { vstack, context ->
    I32ClzExecutor(vstack, context, instruction)
}

fun I32ClzDispatcher(
    instruction: NumericInstruction.I32ClzS,
) = dispatchInstruction { vstack, context ->
    I32ClzExecutor(vstack, context, instruction)
}

fun I32CtzDispatcher(
    instruction: NumericInstruction.I32CtzI,
) = dispatchInstruction { vstack, context ->
    I32CtzExecutor(vstack, context, instruction)
}

fun I32CtzDispatcher(
    instruction: NumericInstruction.I32CtzS,
) = dispatchInstruction { vstack, context ->
    I32CtzExecutor(vstack, context, instruction)
}

fun I32PopcntDispatcher(
    instruction: NumericInstruction.I32PopcntI,
) = dispatchInstruction { vstack, context ->
    I32PopcntExecutor(vstack, context, instruction)
}

fun I32PopcntDispatcher(
    instruction: NumericInstruction.I32PopcntS,
) = dispatchInstruction { vstack, context ->
    I32PopcntExecutor(vstack, context, instruction)
}

fun I32Extend8SDispatcher(
    instruction: NumericInstruction.I32Extend8SI,
) = dispatchInstruction { vstack, context ->
    I32Extend8SExecutor(vstack, context, instruction)
}

fun I32Extend8SDispatcher(
    instruction: NumericInstruction.I32Extend8SS,
) = dispatchInstruction { vstack, context ->
    I32Extend8SExecutor(vstack, context, instruction)
}

fun I32Extend16SDispatcher(
    instruction: NumericInstruction.I32Extend16SI,
) = dispatchInstruction { vstack, context ->
    I32Extend16SExecutor(vstack, context, instruction)
}

fun I32Extend16SDispatcher(
    instruction: NumericInstruction.I32Extend16SS,
) = dispatchInstruction { vstack, context ->
    I32Extend16SExecutor(vstack, context, instruction)
}
