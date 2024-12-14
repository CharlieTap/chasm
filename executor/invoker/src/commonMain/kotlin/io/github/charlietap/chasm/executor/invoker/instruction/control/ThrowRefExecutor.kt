package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction.CatchHandler
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.HandlerDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowRefDispatcher
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.InstructionTag
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.exception
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popFrame
import io.github.charlietap.chasm.executor.runtime.ext.popLabel
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.ext.pushInstruction
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.ext.tagAddress
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun ThrowRefExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.ThrowRef,
): Result<Unit, InvocationError> =
    ThrowRefExecutor(
        context = context,
        instruction = instruction,
        breakDispatcher = ::BrDispatcher,
        handlerDispatcher = ::HandlerDispatcher,
        throwRefDispatcher = ::ThrowRefDispatcher,
    )

internal inline fun ThrowRefExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.ThrowRef,
    crossinline breakDispatcher: Dispatcher<ControlInstruction.Br>,
    crossinline handlerDispatcher: Dispatcher<ExceptionHandler>,
    crossinline throwRefDispatcher: Dispatcher<ControlInstruction.ThrowRef>,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val ref = stack.popReference().bind()

    val exceptionRef = if (ref is ReferenceValue.Null) {
        Err(InvocationError.UnexpectedReferenceValue).bind()
    } else {
        ref as ReferenceValue.Exception
    }

    val instance = store.exception(exceptionRef.address).bind()
    val address = instance.tagAddress

    val handler = jumpToHandlerInstruction(stack).bind()

    if (handler.instructions.isEmpty()) {
        stack.pushValue(ReferenceValue.Exception(exceptionRef.address))
        stack.pushInstruction(throwRefDispatcher(ControlInstruction.ThrowRef))
    } else {

        val frame = stack.peekFrame().bind()

        val catchHandler = handler.instructions.first()
        val otherHandlers = handler.instructions.drop(1)

        val tagMatches = when (catchHandler) {
            is CatchHandler.Catch -> {
                address == frame.instance
                    .tagAddress(catchHandler.tagIndex)
                    .bind()
            }
            is CatchHandler.CatchRef -> {
                address == frame.instance
                    .tagAddress(catchHandler.tagIndex)
                    .bind()
            }
            else -> false
        }

        when {
            catchHandler is CatchHandler.Catch && tagMatches -> {
                instance.fields.asReversed().forEach { field ->
                    stack.pushValue(field)
                }
                stack.pushInstruction(
                    breakDispatcher(ControlInstruction.Br(catchHandler.labelIndex)),
                )
            }
            catchHandler is CatchHandler.CatchRef && tagMatches -> {
                instance.fields.asReversed().forEach { field ->
                    stack.pushValue(field)
                }
                stack.pushValue(exceptionRef)
                stack.pushInstruction(
                    breakDispatcher(ControlInstruction.Br(catchHandler.labelIndex)),
                )
            }
            catchHandler is CatchHandler.CatchAll -> {
                stack.pushInstruction(
                    breakDispatcher(ControlInstruction.Br(catchHandler.labelIndex)),
                )
            }
            catchHandler is CatchHandler.CatchAllRef -> {
                stack.pushValue(exceptionRef)
                stack.pushInstruction(
                    breakDispatcher(ControlInstruction.Br(catchHandler.labelIndex)),
                )
            }
            else -> {
                val elseHandler = ExceptionHandler(otherHandlers)
                val instruction = handlerDispatcher(elseHandler)
                stack.push(Stack.Entry.Instruction(instruction, InstructionTag.HANDLER, elseHandler))
                stack.pushValue(exceptionRef)
                stack.pushInstruction(throwRefDispatcher(ControlInstruction.ThrowRef))
            }
        }
    }
}

private fun jumpToHandlerInstruction(stack: Stack): Result<ExceptionHandler, InvocationError> = binding {

    var instruction: Stack.Entry.Instruction?
    do {
        instruction = stack.popInstructionOrNull()
        when (instruction?.tag) {
            InstructionTag.LABEL -> stack.popLabel().bind()
            InstructionTag.FRAME -> stack.popFrame().bind()
            else -> Unit
        }
    } while (instruction?.tag != InstructionTag.HANDLER && instruction != null)

    if (instruction?.tag == InstructionTag.HANDLER) {
        instruction.handler!!
    } else {
        Err(InvocationError.UncaughtException).bind()
    }
}
