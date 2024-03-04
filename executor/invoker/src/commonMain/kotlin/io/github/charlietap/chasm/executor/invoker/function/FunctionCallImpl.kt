package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun FunctionCallImpl(
    store: Store,
    stack: Stack,
    address: Address.Function,
): Result<Unit, InvocationError> =
    FunctionCallImpl(
        store = store,
        stack = stack,
        address = address,
        functionExecutor = ::FunctionExecutorImpl,
    )

internal fun FunctionCallImpl(
    store: Store,
    stack: Stack,
    address: Address.Function,
    functionExecutor: FunctionExecutor,
): Result<Unit, InvocationError> = binding {

    val instance = store.function(address).bind() as FunctionInstance.WasmFunction
    val type = instance.type

    val locals = buildList {
        repeat(type.params.types.size) {
            stack.popValue()?.value?.let { value ->
                add(value)
            } ?: Err(InvocationError.MissingStackValue).bind<Unit>()
        }
    }.asReversed()

    val arity = Arity(type.results.types.size)
    val frame = Stack.Entry.ActivationFrame(
        arity = arity,
        state = Stack.Entry.ActivationFrame.State(
            locals = locals.toMutableList(),
            module = instance.module,
        ),
    )

    stack.push(frame)

    val label = Stack.Entry.Label(
        arity = arity,
        continuation = instance.function.body.instructions,
    )

    stack.push(label)

    functionExecutor(store, stack).bind()
}
