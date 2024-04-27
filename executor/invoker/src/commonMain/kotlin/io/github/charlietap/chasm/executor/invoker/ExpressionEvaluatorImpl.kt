package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutorImpl
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

fun ExpressionEvaluatorImpl(
    store: Store,
    instance: ModuleInstance,
    expression: Expression,
    arity: Arity.Return = Arity.Return(1),
): Result<ExecutionValue?, InvocationError> =
    ExpressionEvaluatorImpl(
        store = store,
        instance = instance,
        expression = expression,
        arity = arity,
        threadExecutor = ::ThreadExecutorImpl,
    )

fun ExpressionEvaluatorImpl(
    store: Store,
    instance: ModuleInstance,
    expression: Expression,
    arity: Arity.Return,
    threadExecutor: ThreadExecutor,
): Result<ExecutionValue?, InvocationError> {

    val thread = Thread(
        Stack.Entry.ActivationFrame(
            arity,
            0,
            0,
            Stack.Entry.ActivationFrame.State(
                mutableListOf(),
                instance,
            ),
        ),
        expression.instructions.map(::ModuleInstruction),
    )

    val configuration = Configuration(
        store,
        thread,
    )

    return threadExecutor(configuration).map { it.firstOrNull() }
}
