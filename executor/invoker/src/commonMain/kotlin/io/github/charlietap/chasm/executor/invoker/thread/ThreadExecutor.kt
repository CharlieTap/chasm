package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.FrameDispatcher
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal typealias ThreadExecutor = (Configuration) -> Result<List<ExecutionValue>, InvocationError>

internal fun ThreadExecutor(
    configuration: Configuration,
): Result<List<ExecutionValue>, InvocationError> =
    ThreadExecutor(
        configuration = configuration,
        frameDispatcher = ::FrameDispatcher,
    )

internal fun ThreadExecutor(
    configuration: Configuration,
    frameDispatcher: Dispatcher<Stack.Entry.ActivationFrame>,
): Result<List<ExecutionValue>, InvocationError> = binding {

    val thread = configuration.thread
    val stack = Stack()
    val context = ExecutionContext(
        stack = stack,
        store = configuration.store,
        instance = thread.frame.instance,
    )

    stack.push(thread.frame)
    stack.push(frameDispatcher(thread.frame))
    thread.frame.locals.forEach { local ->
        stack.push(Stack.Entry.Value(local))
    }

    stack.push(thread.instructions)

    while (true) {
        val instruction = stack.popInstructionOrNull() ?: break
        instruction(context).bind()
    }

    val results = List(thread.frame.arity.value) {
        stack.popValue().bind().value
    }.asReversed()

    if (stack.size() > 0) {
        Err(InvocationError.ProgramFinishedInconsistentState).bind<List<ExecutionValue>>()
    }

    results
}
