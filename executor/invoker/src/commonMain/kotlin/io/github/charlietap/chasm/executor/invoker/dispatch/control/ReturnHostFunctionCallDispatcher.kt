package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun ReturnHostFunctionCallDispatcher(
    instruction: ControlInstruction.ReturnHostFunctionCall,
) = ReturnHostFunctionCallDispatcher(
    instruction = instruction,
    executor = ::HostFunctionCall,
)

internal inline fun ReturnHostFunctionCallDispatcher(
    instruction: ControlInstruction.ReturnHostFunctionCall,
    crossinline executor: Executor<FunctionInstance.HostFunction>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction.instance)
}
