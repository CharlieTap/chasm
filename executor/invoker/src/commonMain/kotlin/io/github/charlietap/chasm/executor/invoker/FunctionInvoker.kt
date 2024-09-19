package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.ext.functionType
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

typealias FunctionInvoker = (Store, Address.Function, List<ExecutionValue>) -> Result<List<ExecutionValue>, InvocationError>

fun FunctionInvoker(
    store: Store,
    address: Address.Function,
    values: List<ExecutionValue>,
): Result<List<ExecutionValue>, InvocationError> =
    FunctionInvoker(
        store = store,
        address = address,
        values = values,
        threadExecutor = ::ThreadExecutor,
    )

internal fun FunctionInvoker(
    store: Store,
    address: Address.Function,
    values: List<ExecutionValue>,
    threadExecutor: ThreadExecutor,
): Result<List<ExecutionValue>, InvocationError> = binding {

    val function = store.function(address).bind() as FunctionInstance.WasmFunction
    val index = function.module.functionAddresses.indexOf(address)

    if (index == -1) Err(InvocationError.InvalidAddress).bind<List<ExecutionValue>>()

    val functionType = function.functionType().bind()

    val instruction = ControlInstruction.Call(Index.FunctionIndex(index.toUInt()))

    val thread = Thread(
        Stack.Entry.ActivationFrame(
            Arity.Return(functionType.results.types.size),
            0,
            0,
            Stack.Entry.ActivationFrame.State(
                values.toMutableList(),
                function.module,
            ),
        ),
        listOf(ModuleInstruction(instruction)),
    )

    val configuration = Configuration(
        store,
        thread,
    )

    threadExecutor(configuration).bind()
}
