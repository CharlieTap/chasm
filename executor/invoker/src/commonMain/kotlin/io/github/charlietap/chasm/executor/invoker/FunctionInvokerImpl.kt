package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutorImpl
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

fun FunctionInvokerImpl(
    store: Store,
    address: Address.Function,
    values: List<ExecutionValue>,
): Result<List<ExecutionValue>, InvocationError> =
    FunctionInvokerImpl(
        store = store,
        address = address,
        values = values,
        threadExecutor = ::ThreadExecutorImpl,
    )

internal fun FunctionInvokerImpl(
    store: Store,
    address: Address.Function,
    values: List<ExecutionValue>,
    threadExecutor: ThreadExecutor,
): Result<List<ExecutionValue>, InvocationError> = binding {

    val function = store.function(address).bind() as FunctionInstance.WasmFunction
    val index = function.module.functionAddresses.indexOf(address)

    if (index == -1) Err(InvocationError.InvalidAddress).bind<List<ExecutionValue>>()

    val thread = Thread(
        Stack.Entry.ActivationFrame(
            Arity(function.type.results.types.size),
            Stack.Entry.ActivationFrame.State(
                values.toMutableList(),
                function.module,
            ),
        ),
        listOf(ControlInstruction.Call(Index.FunctionIndex(index.toUInt()))),
    )

    val configuration = Configuration(
        store,
        thread,
    )

    threadExecutor(configuration).bind()
}
