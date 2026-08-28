package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ReinterpretI32Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32ReinterpretI32Dispatcher(
    instruction: NumericInstruction.F32ReinterpretI32,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32ReinterpretI32Executor(vstack, context, instruction)
    nextIp
}
