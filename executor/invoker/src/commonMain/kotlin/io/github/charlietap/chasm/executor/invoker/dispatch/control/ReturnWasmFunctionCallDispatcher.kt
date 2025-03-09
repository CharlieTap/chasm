package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instance.FunctionInstance.WasmFunction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun ReturnWasmFunctionCallDispatcher(
    instruction: ControlInstruction.ReturnWasmFunctionCall,
) = ReturnWasmFunctionCallDispatcher(
    instruction = instruction,
    executor = ::ReturnWasmFunctionCall,
)

internal inline fun ReturnWasmFunctionCallDispatcher(
    instruction: ControlInstruction.ReturnWasmFunctionCall,
    crossinline executor: Executor<WasmFunction>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction.instance)
}
