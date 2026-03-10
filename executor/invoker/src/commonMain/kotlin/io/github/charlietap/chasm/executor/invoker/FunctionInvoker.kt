package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.thread.ThreadPointerExecutor
import io.github.charlietap.chasm.executor.invoker.thread.ThreadStackExecutor
import io.github.charlietap.chasm.runtime.Configuration
import io.github.charlietap.chasm.runtime.Thread
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.execution.InterpretationStyle
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.instruction
import io.github.charlietap.chasm.runtime.ext.toExecutionValue
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.StackDepths
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue

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
        threadStackExecutor = ::ThreadStackExecutor,
        threadPointerExecutor = ::ThreadPointerExecutor,
    )

internal inline fun FunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    address: Address.Function,
    values: List<ExecutionValue>,
    crossinline threadStackExecutor: ThreadStackExecutor,
    crossinline threadPointerExecutor: ThreadPointerExecutor = ::ThreadPointerExecutor,
): Result<List<ExecutionValue>, InvocationError> = binding {

    val function = store.function(address) as FunctionInstance.WasmFunction
    val results = when (function.function.body.interpretationStyle) {
        InterpretationStyle.INSTRUCTION_STACK -> {
            val thread = Thread(
                frame = ActivationFrame(
                    arity = function.functionType.results.types.size,
                    instance = function.module,
                    depths = StackDepths(0, 0, 0, 0),
                ),
                instructions = arrayOf(store.instruction(address)),
            )
            val configuration = Configuration(
                store = store,
                thread = thread,
                config = config,
            )
            threadStackExecutor(configuration, values)
        }

        InterpretationStyle.INSTRUCTION_POINTER -> {
            threadPointerExecutor(config, store, function, values)
        }
    }

    results.bind().mapIndexed { idx, result ->
        val expected = function.functionType.results.types[idx]
        result.toExecutionValue(expected)
    }
}
