package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.popFunctionAddress
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun CallRefExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.CallRef,
    returnIp: Int,
): Int {
    val address = vstack.popFunctionAddress()

    return when (val instance = store.function(address)) {
        is FunctionInstance.HostFunction -> {
            HostFunctionCall(vstack, cstack, store, context, instance)
            returnIp
        }
        is FunctionInstance.WasmFunction -> WasmFunctionCall(vstack, cstack, store, context, instance, returnIp)
    }
}
