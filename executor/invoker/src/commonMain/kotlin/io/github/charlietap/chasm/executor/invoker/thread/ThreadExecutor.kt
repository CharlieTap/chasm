package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.instruction.admin.FrameInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal typealias ThreadExecutor = (Configuration, List<ExecutionValue>) -> Result<List<ExecutionValue>, InvocationError>

internal fun ThreadExecutor(
    configuration: Configuration,
    params: List<ExecutionValue>,
): Result<List<ExecutionValue>, InvocationError> =
    ThreadExecutor(
        configuration = configuration,
        params = params,
        frameCleaner = ::FrameInstructionExecutor,
    )

internal fun ThreadExecutor(
    configuration: Configuration,
    params: List<ExecutionValue>,
    frameCleaner: DispatchableInstruction,
): Result<List<ExecutionValue>, InvocationError> = binding {

    val thread = configuration.thread
    val stack = Stack()
    val context = ExecutionContext(
        stack = stack,
        store = configuration.store,
        instance = thread.frame.instance,
        config = configuration.config,
    )

    stack.push(thread.frame)
    params.forEach { local ->
        stack.push(local)
    }

    var loop = true

    @Suppress("UNUSED_PARAMETER")
    fun exitLoop(ctx: ExecutionContext) {
        loop = false
    }

    stack.push(::exitLoop)
    stack.push(frameCleaner)
    stack.push(thread.instructions)

    try {
        while (loop) {
            stack.popInstruction()(context)
        }
    } catch (exception: InvocationException) {
        Err(exception.error).bind()
    }

    if (stack.size() != thread.frame.arity) {
        Err(InvocationError.ProgramFinishedInconsistentState).bind<List<ExecutionValue>>()
    }

    stack.values()
}
