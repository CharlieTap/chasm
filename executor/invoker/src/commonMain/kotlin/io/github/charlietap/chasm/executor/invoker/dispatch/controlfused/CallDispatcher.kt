package io.github.charlietap.chasm.executor.invoker.dispatch.controlfused

import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.CallExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedControlInstruction

fun CallDispatcher(
    instruction: FusedControlInstruction.WasmFunctionCall,
) = CallDispatcher(
    instruction = instruction,
    executor = ::CallExecutor,
)

internal inline fun CallDispatcher(
    instruction: FusedControlInstruction.WasmFunctionCall,
    crossinline executor: Executor<FusedControlInstruction.WasmFunctionCall>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
