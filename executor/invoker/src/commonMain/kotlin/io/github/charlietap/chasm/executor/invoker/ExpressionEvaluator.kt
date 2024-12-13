package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.function.Expression
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

typealias ExpressionEvaluator = (Store, ModuleInstance, Expression, Arity.Return) -> Result<ExecutionValue?, InvocationError>

fun ExpressionEvaluator(
    store: Store,
    instance: ModuleInstance,
    expression: Expression,
    arity: Arity.Return = Arity.Return(1),
): Result<ExecutionValue?, InvocationError> =
    ExpressionEvaluator(
        store = store,
        instance = instance,
        expression = expression,
        arity = arity,
        threadExecutor = ::ThreadExecutor,
    )

internal inline fun ExpressionEvaluator(
    store: Store,
    instance: ModuleInstance,
    expression: Expression,
    arity: Arity.Return,
    crossinline threadExecutor: ThreadExecutor,
): Result<ExecutionValue?, InvocationError> {

    val thread = Thread(
        frame = Stack.Entry.ActivationFrame(
            arity = arity,
            locals = mutableListOf(),
            instance = instance,
            stackHandlersDepth = 0,
            stackInstructionsDepth = 0,
            stackLabelsDepth = 0,
            stackValuesDepth = 0,
        ),
        instructions = expression.instructions,
    )

    val configuration = Configuration(
        store = store,
        thread = thread,
    )

    return threadExecutor(configuration).map { it.firstOrNull() }
}
