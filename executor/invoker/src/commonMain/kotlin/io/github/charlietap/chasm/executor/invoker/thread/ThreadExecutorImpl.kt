package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.instruction.ExecutionInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.ExecutionInstructionExecutorImpl
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
        instructionExecutor = ::ExecutionInstructionExecutorImpl,
    )

internal fun ThreadExecutorImpl(
    configuration: Configuration,
    instructionExecutor: ExecutionInstructionExecutor,
): Result<List<ExecutionValue>, InvocationError> = binding {

    val thread = configuration.thread
    val stack = Stack()

    stack.push(thread.frame)
    thread.frame.state.locals.forEach { local ->
        stack.push(Stack.Entry.Value(local))
    }

    thread.instructions.asReversed().forEach { instruction ->
        stack.push(Stack.Entry.Instruction(instruction))
    }

    while (true) {
        val entry = stack.popInstructionOrNull() ?: break
        instructionExecutor(entry.instruction, configuration.store, stack).bind()
    }

    val results = List(thread.frame.arity.value) {
        stack.popValue().bind().value
    }.asReversed()

    if (stack.size() > 0) {
        Err(InvocationError.ProgramFinishedInconsistentState).bind<List<ExecutionValue>>()
    }

    results
}
