package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.WasmFunctionCallDispatcher
import io.github.charlietap.chasm.executor.invoker.ext.functionType
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
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
        callDispatcher = ::WasmFunctionCallDispatcher,
        threadExecutor = ::ThreadExecutor,
    )

internal inline fun FunctionInvoker(
    store: Store,
    address: Address.Function,
    values: List<ExecutionValue>,
    crossinline callDispatcher: Dispatcher<ControlInstruction.WasmFunctionCall>,
    crossinline threadExecutor: ThreadExecutor,
): Result<List<ExecutionValue>, InvocationError> = binding {

    val function = store.function(address).bind() as FunctionInstance.WasmFunction
    val index = function.module.functionAddresses.indexOf(address)

    if (index == -1) Err(InvocationError.InvalidAddress).bind<List<ExecutionValue>>()

    val functionType = function.functionType().bind()
    val instruction = callDispatcher(
        ControlInstruction.WasmFunctionCall(function),
    )

    val thread = Thread(
        frame = Stack.Entry.ActivationFrame(
            arity = Arity.Return(functionType.results.types.size),
            instance = function.module,
            locals = values.toMutableList(),
            stackHandlersDepth = 0,
            stackInstructionsDepth = 0,
            stackLabelsDepth = 0,
            stackValuesDepth = 0,
        ),
        instructions = listOf(instruction),
    )

    val configuration = Configuration(
        store = store,
        thread = thread,
    )

    threadExecutor(configuration).bind()
}
