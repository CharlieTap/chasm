package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun WasmFunctionCallDispatcher(
    instruction: ControlInstruction.WasmFunctionCall,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, context, nextIp ->
    WasmFunctionCall(vstack, cstack, store, context, instruction.instance, nextIp)
}
