package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64Add128Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64MulWideSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64MulWideUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64Sub128Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Iiii,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Iiis,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Iisi,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Iiss,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Isii,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Isis,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Issi,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Isss,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Siii,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Siis,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Sisi,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Siss,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Ssii,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Ssis,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Sssi,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128Ssss,
) = dispatchInstruction { vstack, context ->
    I64Add128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Iiii,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Iiis,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Iisi,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Iiss,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Isii,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Isis,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Issi,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Isss,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Siii,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Siis,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Sisi,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Siss,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Ssii,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Ssis,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Sssi,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128Ssss,
) = dispatchInstruction { vstack, context ->
    I64Sub128Executor(vstack, context, instruction)
}

fun I64MulWideSDispatcher(
    instruction: NumericInstruction.I64MulWideSIi,
) = dispatchInstruction { vstack, context ->
    I64MulWideSExecutor(vstack, context, instruction)
}

fun I64MulWideSDispatcher(
    instruction: NumericInstruction.I64MulWideSIs,
) = dispatchInstruction { vstack, context ->
    I64MulWideSExecutor(vstack, context, instruction)
}

fun I64MulWideSDispatcher(
    instruction: NumericInstruction.I64MulWideSSi,
) = dispatchInstruction { vstack, context ->
    I64MulWideSExecutor(vstack, context, instruction)
}

fun I64MulWideSDispatcher(
    instruction: NumericInstruction.I64MulWideSSs,
) = dispatchInstruction { vstack, context ->
    I64MulWideSExecutor(vstack, context, instruction)
}

fun I64MulWideUDispatcher(
    instruction: NumericInstruction.I64MulWideUIi,
) = dispatchInstruction { vstack, context ->
    I64MulWideUExecutor(vstack, context, instruction)
}

fun I64MulWideUDispatcher(
    instruction: NumericInstruction.I64MulWideUIs,
) = dispatchInstruction { vstack, context ->
    I64MulWideUExecutor(vstack, context, instruction)
}

fun I64MulWideUDispatcher(
    instruction: NumericInstruction.I64MulWideUSi,
) = dispatchInstruction { vstack, context ->
    I64MulWideUExecutor(vstack, context, instruction)
}

fun I64MulWideUDispatcher(
    instruction: NumericInstruction.I64MulWideUSs,
) = dispatchInstruction { vstack, context ->
    I64MulWideUExecutor(vstack, context, instruction)
}
