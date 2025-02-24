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
import io.github.charlietap.chasm.executor.runtime.ext.toLongFromBoxed
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
    val cstack = ControlStack()
    val vstack = ValueStack()
    val istack = cstack.instructionStack()
    val context = ExecutionContext(
        cstack = cstack,
        vstack = vstack,
        store = configuration.store,
        instance = thread.frame.instance,
        config = configuration.config,
    )

    cstack.push(thread.frame)
    params.forEach { param ->
        vstack.push(param.toLongFromBoxed())
    }

    var loop = true

    @Suppress("UNUSED_PARAMETER")
    fun exitLoop(ctx: ExecutionContext) {
        loop = false
    }

    istack.push(::exitLoop)
    istack.push(frameCleaner)
    istack.pushAll(thread.instructions)

    try {
        while (loop) {
            istack.pop()(context)
        }
    } catch (exception: InvocationException) {
        Err(exception.error).bind()
    }

    if (context.depth() != thread.frame.arity) {
        Err(InvocationError.ProgramFinishedInconsistentState).bind<List<ExecutionValue>>()
    }

    List(thread.frame.arity) {
        vstack.pop()
    }.asReversed()
}
