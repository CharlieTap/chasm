package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32ReinterpretF32Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32ReinterpretF32Dispatcher(
    instruction: NumericInstruction.I32ReinterpretF32,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32ReinterpretF32Executor(vstack, context, instruction)
    nextIp
}
