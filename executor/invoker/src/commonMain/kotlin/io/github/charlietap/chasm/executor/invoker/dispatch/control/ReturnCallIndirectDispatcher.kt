package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnCallIndirectExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun ReturnCallIndirectDispatcher(
    instruction: ControlInstruction.ReturnCallIndirect,
) = ReturnCallIndirectDispatcher(
    instruction = instruction,
    executor = ::ReturnCallIndirectExecutor,
)

internal inline fun ReturnCallIndirectDispatcher(
    instruction: ControlInstruction.ReturnCallIndirect,
    crossinline executor: Executor<ControlInstruction.ReturnCallIndirect>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
