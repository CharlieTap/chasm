package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun ReturnHostFunctionCallDispatcher(
    instruction: ControlInstruction.ReturnHostFunctionCall,
) = ReturnHostFunctionCallDispatcher(
    instruction = instruction,
    executor = ::HostFunctionCall,
)

internal inline fun ReturnHostFunctionCallDispatcher(
    instruction: ControlInstruction.ReturnHostFunctionCall,
    crossinline executor: Executor<FunctionInstance.HostFunction>,
): DispatchableInstruction = { context ->
    executor(context, instruction.instance)
}
