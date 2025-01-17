package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.function.Expression
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.executor.runtime.stack.FrameStackDepths
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

typealias ExpressionEvaluator = (RuntimeConfig, Store, ModuleInstance, Expression, Arity.Return) -> Result<ExecutionValue?, InvocationError>

fun ExpressionEvaluator(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    expression: Expression,
    arity: Arity.Return = Arity.Return(1),
): Result<ExecutionValue?, InvocationError> =
    ExpressionEvaluator(
        config = config,
        store = store,
        instance = instance,
        expression = expression,
        arity = arity,
        threadExecutor = ::ThreadExecutor,
    )

internal inline fun ExpressionEvaluator(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    expression: Expression,
    arity: Arity.Return,
    crossinline threadExecutor: ThreadExecutor,
): Result<ExecutionValue?, InvocationError> {

    val thread = Thread(
        frame = ActivationFrame(
            arity = arity.value,
            instance = instance,
            depths = FrameStackDepths(0, 0, 0, 0),
        ),
        instructions = expression.instructions,
    )

    val configuration = Configuration(
        config = config,
        store = store,
        thread = thread,
    )

    return threadExecutor(configuration, emptyList()).map { it.firstOrNull() }
}
