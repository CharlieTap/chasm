package io.github.charlietap.chasm.executor.invoker.drop

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.function.StackFunctionBody
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.store.Store

fun StackFunctionRetirer(
    store: Store,
    address: Address.Function,
) {
    val function = store.functions.getOrNull(address.address) as? FunctionInstance.StackFunction ?: return
    store.functions[address.address] = function.copy(body = DEAD_STACK_FUNCTION_BODY)
    if (address.address in store.instructions.indices) {
        store.instructions[address.address] = DEAD_FUNCTION
    }
}

private val DEAD_STACK_FUNCTION_BODY = StackFunctionBody { _, _, _, _ ->
    throw InvocationException(InvocationError.InvocationOfADeinstantiatedInstance)
}

private val DEAD_FUNCTION = DispatchableInstruction { _, _, _, _ ->
    throw InvocationException(InvocationError.InvocationOfADeinstantiatedInstance)
}
