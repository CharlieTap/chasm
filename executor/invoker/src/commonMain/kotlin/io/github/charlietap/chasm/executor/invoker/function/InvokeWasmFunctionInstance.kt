package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.dispatch.control.WasmFunctionCallDispatcher
import io.github.charlietap.chasm.executor.invoker.thread.ThreadPointerExecutor
import io.github.charlietap.chasm.executor.invoker.thread.ThreadStackExecutor
import io.github.charlietap.chasm.runtime.Configuration
import io.github.charlietap.chasm.runtime.Thread
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.execution.InterpretationStyle
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.StackDepths
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue

internal fun InvokeWasmFunctionInstance(
    config: RuntimeConfig,
    store: Store,
    instance: FunctionInstance.WasmFunction,
    values: List<ExecutionValue>,
): Result<List<Long>, InvocationError> =
    InvokeWasmFunctionInstance(
        config = config,
        store = store,
        instance = instance,
        values = values,
        stackThreadStackExecutor = ::ThreadStackExecutor,
        threadPointerExecutor = ::ThreadPointerExecutor,
    )

internal inline fun InvokeWasmFunctionInstance(
    config: RuntimeConfig,
    store: Store,
    instance: FunctionInstance.WasmFunction,
    values: List<ExecutionValue>,
    crossinline stackThreadStackExecutor: ThreadStackExecutor,
    crossinline threadPointerExecutor: ThreadPointerExecutor,
): Result<List<Long>, InvocationError> {
    val body = instance.function.body

    return if (body.interpretationStyle == InterpretationStyle.INSTRUCTION_POINTER) {
        threadPointerExecutor(config, store, instance, values)
    } else {
        val thread = Thread(
            frame = ActivationFrame(
                arity = instance.functionType.results.types.size,
                instance = instance.module,
                depths = StackDepths(0, 0, 0, 0),
            ),
            instructions = arrayOf(
                WasmFunctionCallDispatcher(
                    ControlInstruction.WasmFunctionCall(instance),
                ),
            ),
        )

        val configuration = Configuration(
            store = store,
            thread = thread,
            config = config,
        )

        stackThreadStackExecutor(configuration, values)
    }
}
