package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.ext.instruction
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.executor.runtime.stack.FrameStackDepths
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

typealias FunctionInvoker = (RuntimeConfig, Store, Address.Function, List<ExecutionValue>) -> Result<List<ExecutionValue>, InvocationError>

fun FunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    address: Address.Function,
    values: List<ExecutionValue>,
): Result<List<ExecutionValue>, InvocationError> =
    FunctionInvoker(
        config = config,
        store = store,
        address = address,
        values = values,
        threadExecutor = ::ThreadExecutor,
    )

internal inline fun FunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    address: Address.Function,
    values: List<ExecutionValue>,
    crossinline threadExecutor: ThreadExecutor,
): Result<List<ExecutionValue>, InvocationError> = binding {

    val function = store.function(address).bind() as FunctionInstance.WasmFunction
    val instruction = store.instruction(address).bind()
    val arity = function.functionType.results.types.size

    val thread = Thread(
        frame = ActivationFrame(
            arity = arity,
            instance = function.module,
            locals = values.toMutableList(),
            depths = FrameStackDepths(0, 0, 0, 0),
        ),
        instructions = arrayOf(instruction),
    )

    val configuration = Configuration(
        store = store,
        thread = thread,
        config = config,
    )

    threadExecutor(configuration).bind()
}
