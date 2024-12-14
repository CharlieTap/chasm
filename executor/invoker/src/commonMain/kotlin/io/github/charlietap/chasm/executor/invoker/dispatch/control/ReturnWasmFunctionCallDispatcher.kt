package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance.WasmFunction
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun ReturnWasmFunctionCallDispatcher(
    instruction: ControlInstruction.ReturnWasmFunctionCall,
) = ReturnWasmFunctionCallDispatcher(
    instruction = instruction,
    executor = ::ReturnWasmFunctionCall,
)

internal inline fun ReturnWasmFunctionCallDispatcher(
    instruction: ControlInstruction.ReturnWasmFunctionCall,
    crossinline executor: Executor<WasmFunction>,
): DispatchableInstruction = { context ->
    executor(context, instruction.instance)
}
