package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.toFunctionAddress
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT

internal fun CallIndirectExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.CallIndirect,
    returnIp: Int,
): Int = CallIndirectExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    table = instruction.table,
    type = instruction.type,
    returnIp = returnIp,
    hostFunctionCall = ::HostFunctionCall,
    wasmFunctionCall = ::WasmFunctionCall,
)

internal inline fun CallIndirectExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    table: TableInstance,
    type: RTT,
    returnIp: Int,
    crossinline hostFunctionCall: HostFunctionCall,
    crossinline wasmFunctionCall: WasmFunctionCall,
): Int {
    val elementIndex = vstack.popI32()
    val address = table.element(elementIndex).toFunctionAddress()

    val functionInstance = store.function(address)
    if (!context.heap.matchesRuntimeType(functionInstance.rtt, type)) {
        throw InvocationException(InvocationError.IndirectCallHasIncorrectFunctionType)
    }

    return when (functionInstance) {
        is FunctionInstance.HostFunction -> {
            hostFunctionCall(vstack, cstack, store, context, functionInstance)
            returnIp
        }
        is FunctionInstance.WasmFunction -> wasmFunctionCall(vstack, cstack, store, context, functionInstance, returnIp)
    }
}
