package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.function.ReturnHostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.toFunctionAddress
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun ReturnCallIndirectExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.ReturnCallIndirect,
): Int {
    val elementIndex = vstack.popI32()
    val address = instruction.table.element(elementIndex).toFunctionAddress()
    val function = store.function(address)
    val actualType = function.rtt
    if (actualType !== instruction.type && actualType.superTypes.none { it === instruction.type }) {
        throw InvocationException(InvocationError.IndirectCallHasIncorrectFunctionType)
    }

    return when (function) {
        is FunctionInstance.HostFunction -> ReturnHostFunctionCall(vstack, cstack, store, context, function)
        is FunctionInstance.WasmFunction -> ReturnWasmFunctionCall(vstack, cstack, store, context, function)
    }
}
