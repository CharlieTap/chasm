package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.popFunctionAddress
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun ReturnCallRefExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.ReturnCallRef,
) = ReturnCallRefExecutor(
    ip = ip,
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    hostFunctionCall = ::HostFunctionCall,
    wasmFunctionCall = ::ReturnWasmFunctionCall,
)

internal inline fun ReturnCallRefExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    crossinline hostFunctionCall: HostFunctionCall,
    crossinline wasmFunctionCall: WasmFunctionCall,
): InstructionPointer {
    val address = vstack.popFunctionAddress()

    return when (val instance = store.function(address)) {
        is FunctionInstance.HostFunction -> hostFunctionCall(ip, vstack, cstack, store, context, instance)
        is FunctionInstance.WasmFunction -> wasmFunctionCall(ip, vstack, cstack, store, context, instance)
    }
}
