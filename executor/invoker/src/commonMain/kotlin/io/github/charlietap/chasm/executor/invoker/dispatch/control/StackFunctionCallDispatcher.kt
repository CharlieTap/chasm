package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.function.StackFunctionCall
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun StackFunctionCallDispatcher(
    instruction: ControlInstruction.StackFunctionCall,
) = StackFunctionCallDispatcher(
    instruction = instruction,
    executor = ::StackFunctionCall,
)

internal inline fun StackFunctionCallDispatcher(
    instruction: ControlInstruction.StackFunctionCall,
    crossinline executor: Executor<FunctionInstance.StackFunction>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction.instance)
}
