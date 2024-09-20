package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.instruction.ExecutionInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instruction.ExecutionInstruction
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal typealias ThreadExecutor = (Configuration) -> Result<List<ExecutionValue>, InvocationError>

internal fun ThreadExecutor(
    configuration: Configuration,
): Result<List<ExecutionValue>, InvocationError> =
    ThreadExecutor(
        configuration = configuration,
        instructionExecutor = ::ExecutionInstructionExecutor,
    )

internal fun ThreadExecutor(
    configuration: Configuration,
    instructionExecutor: Executor<ExecutionInstruction>,
): Result<List<ExecutionValue>, InvocationError> = binding {

    val thread = configuration.thread
    val stack = Stack()
    val context = ExecutionContext(
        stack = stack,
        store = configuration.store,
    )

    stack.push(thread.frame)
    thread.frame.state.locals.forEach { local ->
        stack.push(Stack.Entry.Value(local))
    }

    thread.instructions.asReversed().forEach { instruction ->
        stack.push(Stack.Entry.Instruction(instruction))
    }

    while (true) {
        val entry = stack.popInstructionOrNull() ?: break
        instructionExecutor(context, entry.instruction).bind()
    }

    val results = List(thread.frame.arity.value) {
        stack.popValue().bind().value
    }.asReversed()

    if (stack.size() > 0) {
        Err(InvocationError.ProgramFinishedInconsistentState).bind<List<ExecutionValue>>()
    }

    results
}
