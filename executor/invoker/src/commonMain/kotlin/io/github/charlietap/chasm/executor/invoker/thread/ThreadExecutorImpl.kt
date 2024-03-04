package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionExecutorImpl
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

fun ThreadExecutorImpl(
    configuration: Configuration,
): Result<List<ExecutionValue>, InvocationError> =
    ThreadExecutorImpl(
        configuration = configuration,
        instructionExecutor = ::InstructionExecutorImpl,
    )

fun ThreadExecutorImpl(
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

    val results = buildList {
        repeat(thread.frame.arity.value) {
            stack.popValue()?.let { entry ->
                add(entry.value)
            }
        }
    }

    if (results.size != thread.frame.arity.value) {
        Err(InvocationError.FunctionReturnArityMismatch).bind<List<ExecutionValue>>()
    }

    val frame = stack.popFrame()

    if (frame != thread.frame) {
        Err(InvocationError.MissingStackFrame).bind<List<ExecutionValue>>()
    }

    results
}
