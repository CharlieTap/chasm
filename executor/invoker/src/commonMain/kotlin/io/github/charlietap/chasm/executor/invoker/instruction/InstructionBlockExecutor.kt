package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.HandlerDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.LabelDispatcher
import io.github.charlietap.chasm.executor.invoker.ext.forEachReversed
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.Instruction
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.InstructionTag
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal typealias InstructionBlockExecutor = (Stack, Stack.Entry.Label, List<DispatchableInstruction>, List<ExecutionValue>, ExceptionHandler?) -> Result<Unit, InvocationError>

internal inline fun InstructionBlockExecutor(
    stack: Stack,
    label: Stack.Entry.Label,
    instructions: List<DispatchableInstruction>,
    params: List<ExecutionValue>,
    handler: ExceptionHandler?,
): Result<Unit, InvocationError> =
    InstructionBlockExecutor(
        stack = stack,
        label = label,
        instructions = instructions,
        params = params,
        handler = handler,
        handlerDispatcher = ::HandlerDispatcher,
        labelDispatcher = ::LabelDispatcher,
    )

internal inline fun InstructionBlockExecutor(
    stack: Stack,
    label: Stack.Entry.Label,
    instructions: List<DispatchableInstruction>,
    params: List<ExecutionValue>,
    handler: ExceptionHandler?,
    crossinline handlerDispatcher: Dispatcher<ExceptionHandler>,
    crossinline labelDispatcher: Dispatcher<Stack.Entry.Label>,
): Result<Unit, InvocationError> = binding {

    handler?.let {
        val instruction = handlerDispatcher(handler)
        stack.push(Instruction(instruction, InstructionTag.HANDLER, handler))
    }
    stack.push(label)
    stack.push(Instruction(labelDispatcher(label), InstructionTag.LABEL))

    params.forEachReversed { value ->
        stack.push(Stack.Entry.Value(value))
    }

    instructions.forEachReversed { instruction ->
        stack.push(Instruction(instruction))
    }
}
