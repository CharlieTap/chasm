package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionExecutorImpl
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal fun ThreadExecutorImpl(
    configuration: Configuration,
): Result<List<ExecutionValue>, InvocationError> =
    ThreadExecutorImpl(
        configuration = configuration,
        instructionExecutor = ::InstructionExecutorImpl,
    )

internal fun ThreadExecutorImpl(
    configuration: Configuration,
    instructionExecutor: InstructionExecutor,
): Result<List<ExecutionValue>, InvocationError> = binding {

    val thread = configuration.thread
    val stack = Stack()

    stack.push(thread.frame)
    thread.frame.state.locals.forEach { local ->
        stack.push(Stack.Entry.Value(local))
    }

    thread.instructions.forEach { instruction ->
        instructionExecutor(instruction, configuration.store, stack).bind()
    }

    val results = List(thread.frame.arity.value) {
        stack.popValue().bind().value
    }.asReversed()

    val frame = stack.popFrameOrNull()

    if (frame != thread.frame) {
        Err(InvocationError.MissingStackFrame).bind<List<ExecutionValue>>()
    }

    if (stack.size() > 0) {
        Err(InvocationError.ProgramFinishedInconsistentState).bind<List<ExecutionValue>>()
    }

    results
}
