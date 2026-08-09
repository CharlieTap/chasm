package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.function.ReturnHostFunctionCall
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun ReturnHostFunctionCallDispatcher(
    instruction: ControlInstruction.ReturnHostFunctionCall,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, context, _ ->
    ReturnHostFunctionCall(vstack, cstack, store, context, instruction.instance)
}
