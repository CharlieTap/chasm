package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64ClzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64CtzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64Extend16SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64Extend32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64Extend8SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64PopcntExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64ClzDispatcher(
    instruction: NumericInstruction.I64ClzI,
) = dispatchInstruction { vstack, context ->
    I64ClzExecutor(vstack, context, instruction)
}

fun I64ClzDispatcher(
    instruction: NumericInstruction.I64ClzS,
) = dispatchInstruction { vstack, context ->
    I64ClzExecutor(vstack, context, instruction)
}

fun I64CtzDispatcher(
    instruction: NumericInstruction.I64CtzI,
) = dispatchInstruction { vstack, context ->
    I64CtzExecutor(vstack, context, instruction)
}

fun I64CtzDispatcher(
    instruction: NumericInstruction.I64CtzS,
) = dispatchInstruction { vstack, context ->
    I64CtzExecutor(vstack, context, instruction)
}

fun I64PopcntDispatcher(
    instruction: NumericInstruction.I64PopcntI,
) = dispatchInstruction { vstack, context ->
    I64PopcntExecutor(vstack, context, instruction)
}

fun I64PopcntDispatcher(
    instruction: NumericInstruction.I64PopcntS,
) = dispatchInstruction { vstack, context ->
    I64PopcntExecutor(vstack, context, instruction)
}

fun I64Extend8SDispatcher(
    instruction: NumericInstruction.I64Extend8SI,
) = dispatchInstruction { vstack, context ->
    I64Extend8SExecutor(vstack, context, instruction)
}

fun I64Extend8SDispatcher(
    instruction: NumericInstruction.I64Extend8SS,
) = dispatchInstruction { vstack, context ->
    I64Extend8SExecutor(vstack, context, instruction)
}

fun I64Extend16SDispatcher(
    instruction: NumericInstruction.I64Extend16SI,
) = dispatchInstruction { vstack, context ->
    I64Extend16SExecutor(vstack, context, instruction)
}

fun I64Extend16SDispatcher(
    instruction: NumericInstruction.I64Extend16SS,
) = dispatchInstruction { vstack, context ->
    I64Extend16SExecutor(vstack, context, instruction)
}

fun I64Extend32SDispatcher(
    instruction: NumericInstruction.I64Extend32SI,
) = dispatchInstruction { vstack, context ->
    I64Extend32SExecutor(vstack, context, instruction)
}

fun I64Extend32SDispatcher(
    instruction: NumericInstruction.I64Extend32SS,
) = dispatchInstruction { vstack, context ->
    I64Extend32SExecutor(vstack, context, instruction)
}
