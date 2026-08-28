package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64ExtendI32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64ExtendI32UExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64ExtendI32SDispatcher(
    instruction: NumericInstruction.I64ExtendI32SI,
) = dispatchInstruction { vstack, context ->
    I64ExtendI32SExecutor(vstack, context, instruction)
}

fun I64ExtendI32SDispatcher(
    instruction: NumericInstruction.I64ExtendI32SS,
) = dispatchInstruction { vstack, context ->
    I64ExtendI32SExecutor(vstack, context, instruction)
}

fun I64ExtendI32UDispatcher(
    instruction: NumericInstruction.I64ExtendI32UI,
) = dispatchInstruction { vstack, context ->
    I64ExtendI32UExecutor(vstack, context, instruction)
}

fun I64ExtendI32UDispatcher(
    instruction: NumericInstruction.I64ExtendI32US,
) = dispatchInstruction { vstack, context ->
    I64ExtendI32UExecutor(vstack, context, instruction)
}
