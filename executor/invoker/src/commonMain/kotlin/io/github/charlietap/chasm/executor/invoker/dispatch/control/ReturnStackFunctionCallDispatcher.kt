package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.function.ReturnStackFunctionCall
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun ReturnStackFunctionCallDispatcher(
    instruction: ControlInstruction.ReturnStackFunctionCall,
) = ReturnStackFunctionCallDispatcher(
    instruction = instruction,
    executor = ::ReturnStackFunctionCall,
)

internal inline fun ReturnStackFunctionCallDispatcher(
    instruction: ControlInstruction.ReturnStackFunctionCall,
    crossinline executor: Executor<FunctionInstance.StackFunction>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction.instance)
}
