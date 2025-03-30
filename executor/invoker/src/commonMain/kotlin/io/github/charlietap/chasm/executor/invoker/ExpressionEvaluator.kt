package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnExpressionDispatcher
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.runtime.Arity
import io.github.charlietap.chasm.runtime.Configuration
import io.github.charlietap.chasm.runtime.Thread
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.function.Expression
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.StackDepths
import io.github.charlietap.chasm.runtime.store.Store

typealias ExpressionEvaluator = (RuntimeConfig, Store, ModuleInstance, Expression, Arity.Return) -> Result<Long?, InvocationError>

fun ExpressionEvaluator(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    expression: Expression,
    arity: Arity.Return = Arity.Return(1),
): Result<Long?, InvocationError> =
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
): Result<Long?, InvocationError> {

    val thread = Thread(
        frame = ActivationFrame(
            arity = arity.value,
            instance = instance,
            depths = StackDepths(0, 0),
            previousInstructions = emptyArray(),
            previousInstructionPointer = InstructionPointer(-2),
            previousFramePointer = 0,
        ),
        instructions = Array(expression.instructions.size + 1) { idx ->
            if (idx < expression.instructions.size) {
                expression.instructions[idx]
            } else {
                ReturnExpressionDispatcher(ControlInstruction.ReturnExpression)
            }
        },
    )

    val configuration = Configuration(
        config = config,
        store = store,
        thread = thread,
    )

    return threadExecutor(configuration, emptyList(), longArrayOf()).map { it.firstOrNull() }
}
