package io.github.charlietap.chasm.executor.invoker.dispatch.controlfused

import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.CallExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedControlInstruction

fun CallDispatcher(
    instruction: FusedControlInstruction.WasmFunctionCall,
) = CallDispatcher(
    instruction = instruction,
    executor = ::CallExecutor,
)

internal inline fun CallDispatcher(
    instruction: FusedControlInstruction.WasmFunctionCall,
    crossinline executor: Executor<FusedControlInstruction.WasmFunctionCall>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
