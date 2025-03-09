package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instance.FunctionInstance.WasmFunction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun WasmFunctionCallDispatcher(
    instruction: ControlInstruction.WasmFunctionCall,
) = WasmFunctionCallDispatcher(
    instruction = instruction,
    executor = ::WasmFunctionCall,
)

internal inline fun WasmFunctionCallDispatcher(
    instruction: ControlInstruction.WasmFunctionCall,
    crossinline executor: Executor<WasmFunction>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction.instance)
}
