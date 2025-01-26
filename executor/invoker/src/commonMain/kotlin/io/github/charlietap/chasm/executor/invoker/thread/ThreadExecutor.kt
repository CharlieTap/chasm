package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.instruction.admin.FrameInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.depth
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.stack.ControlStack
import io.github.charlietap.chasm.executor.runtime.stack.ValueStack
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal typealias ThreadExecutor = (Configuration, List<ExecutionValue>) -> Result<List<Long>, InvocationError>

internal fun ThreadExecutor(
    configuration: Configuration,
    params: List<ExecutionValue>,
): Result<List<Long>, InvocationError> =
    ThreadExecutor(
        configuration = configuration,
        params = params,
        frameCleaner = ::FrameInstructionExecutor,
    )

internal fun ThreadExecutor(
    configuration: Configuration,
    params: List<ExecutionValue>,
    frameCleaner: DispatchableInstruction,
): Result<List<Long>, InvocationError> = binding {

    val thread = configuration.thread
    val controlStack = ControlStack()
    val valueStack = ValueStack()
    val context = ExecutionContext(
        cstack = controlStack,
        vstack = valueStack,
        store = configuration.store,
        instance = thread.frame.instance,
        config = configuration.config,
    )

    controlStack.push(thread.frame)
    params.forEach { param ->
        valueStack.push(param.toLong())
    }

    var loop = true

    @Suppress("UNUSED_PARAMETER")
    fun exitLoop(ctx: ExecutionContext) {
        loop = false
    }

    controlStack.push(::exitLoop)
    controlStack.push(frameCleaner)
    controlStack.push(thread.instructions)

    try {
        while (loop) {
            controlStack.popInstruction()(context)
        }
    } catch (exception: InvocationException) {
        Err(exception.error).bind()
    }

    if (context.depth() != thread.frame.arity) {
        Err(InvocationError.ProgramFinishedInconsistentState).bind<List<ExecutionValue>>()
    }

    List(thread.frame.arity) {
        valueStack.pop()
    }.asReversed()
}
