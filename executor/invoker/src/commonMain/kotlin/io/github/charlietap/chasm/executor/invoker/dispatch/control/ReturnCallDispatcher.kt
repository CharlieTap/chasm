package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnCallExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun ReturnCallDispatcher(
    instruction: ControlInstruction.ReturnCall,
) = ReturnCallDispatcher(
    instruction = instruction,
    executor = ::ReturnCallExecutor,
)

internal inline fun ReturnCallDispatcher(
    instruction: ControlInstruction.ReturnCall,
    crossinline executor: Executor<ControlInstruction.ReturnCall>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
