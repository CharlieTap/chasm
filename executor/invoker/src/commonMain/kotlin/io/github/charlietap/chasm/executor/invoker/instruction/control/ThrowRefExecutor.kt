package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction.CatchHandler
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.HandlerDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowRefDispatcher
import io.github.charlietap.chasm.executor.invoker.ext.forEachReversed
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.exception
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popHandler
import io.github.charlietap.chasm.executor.runtime.ext.popReference
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
        stack.push(throwRefDispatcher(ControlInstruction.ThrowRef))
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
                instance.fields.forEachReversed { field ->
                    stack.pushValue(field)
                }
                stack.push(
                    breakDispatcher(ControlInstruction.Br(catchHandler.labelIndex)),
                )
            }
            catchHandler is CatchHandler.CatchRef && tagMatches -> {
                instance.fields.forEachReversed { field ->
                    stack.pushValue(field)
                }
                stack.pushValue(exceptionRef)
                stack.push(
                    breakDispatcher(ControlInstruction.Br(catchHandler.labelIndex)),
                )
            }
            catchHandler is CatchHandler.CatchAll -> {
                stack.push(
                    breakDispatcher(ControlInstruction.Br(catchHandler.labelIndex)),
                )
            }
            catchHandler is CatchHandler.CatchAllRef -> {
                stack.pushValue(exceptionRef)
                stack.push(
                    breakDispatcher(ControlInstruction.Br(catchHandler.labelIndex)),
                )
            }
            else -> {

                handler.instructions = otherHandlers
                stack.push(handler)
                val instruction = handlerDispatcher(handler)
                stack.push(instruction)
                stack.pushValue(exceptionRef)
                stack.push(throwRefDispatcher(ControlInstruction.ThrowRef))
            }
        }
    }
}

private fun jumpToHandlerInstruction(stack: Stack): Result<ExceptionHandler, InvocationError> = binding {

    val handler = stack.popHandler().bind()

    stack.shrinkLabels(0, handler.labelsDepth)
    stack.shrinkFrames(0, handler.framesDepth)
    stack.shrinkInstructions(0, handler.instructionsDepth)

    handler
}
