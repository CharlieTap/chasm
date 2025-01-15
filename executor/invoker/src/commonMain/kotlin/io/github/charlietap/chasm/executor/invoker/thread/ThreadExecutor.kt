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

internal typealias ThreadExecutor = (Configuration) -> Result<List<ExecutionValue>, InvocationError>

internal fun ThreadExecutor(
    configuration: Configuration,
): Result<List<ExecutionValue>, InvocationError> =
    ThreadExecutor(
        configuration = configuration,
        frameCleaner = ::FrameInstructionExecutor,
    )

internal fun ThreadExecutor(
    configuration: Configuration,
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
    stack.push(frameCleaner)
    thread.frame.locals.forEach { local ->
        stack.push(local)
    }

    stack.push(thread.instructions)

    try {
        while (stack.instructionsDepth() > 0) {
            val instruction = stack.popInstructionOrNull() ?: break
            instruction(context)
        }
    } catch (exception: InvocationException) {
        Err(exception.error).bind()
    }

    if (stack.size() != thread.frame.arity) {
        Err(InvocationError.ProgramFinishedInconsistentState).bind<List<ExecutionValue>>()
    }

    stack.values()
}
