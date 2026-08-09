package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun ReturnWasmFunctionCallDispatcher(
    instruction: ControlInstruction.ReturnWasmFunctionCall,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, context, _ ->
    ReturnWasmFunctionCall(vstack, cstack, store, context, instruction.instance)
}
