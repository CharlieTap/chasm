package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.ast.instruction.ControlInstruction.CatchHandler
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.HandlerDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowRefDispatcher
import io.github.charlietap.chasm.executor.invoker.ext.tagAddress
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.exception
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.ext.pushReference
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.stack.ControlStack
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun ThrowRefExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.ThrowRef,
) = ThrowRefExecutor(
    context = context,
    instruction = instruction,
    breakDispatcher = ::BrDispatcher,
    handlerDispatcher = ::HandlerDispatcher,
)

internal inline fun ThrowRefExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.ThrowRef,
    crossinline breakDispatcher: Dispatcher<ControlInstruction.Br>,
    crossinline handlerDispatcher: Dispatcher<ExceptionHandler>,
) {
    val stack = context.vstack
    val cstack = context.cstack
    val store = context.store

    val ref = stack.popReference()

    val exceptionRef = if (ref is ReferenceValue.Null) {
        throw InvocationException(InvocationError.UnexpectedReferenceValue)
    } else {
        ref as ReferenceValue.Exception
    }

    val instance = store.exception(exceptionRef.address)
    val address = instance.tagAddress

    val handler = jumpToHandlerInstruction(cstack)

    if (handler.instructions.isEmpty()) {
        stack.pushReference(ReferenceValue.Exception(exceptionRef.address))
        cstack.push(ThrowRefDispatcher(ControlInstruction.ThrowRef))
    } else {

        val frame = context.cstack.peekFrame()

        val catchHandler = handler.instructions.first()
        val otherHandlers = handler.instructions.drop(1)

        val tagMatches = when (catchHandler) {
            is CatchHandler.Catch -> {
                address == frame.instance
                    .tagAddress(catchHandler.tagIndex)
            }
            is CatchHandler.CatchRef -> {
                address == frame.instance
                    .tagAddress(catchHandler.tagIndex)
            }
            else -> false
        }

        when {
            catchHandler is CatchHandler.Catch && tagMatches -> {
                instance.fields.reverse()
                stack.push(instance.fields)
                cstack.push(
                    breakDispatcher(ControlInstruction.Br(catchHandler.labelIndex)),
                )
            }
            catchHandler is CatchHandler.CatchRef && tagMatches -> {
                instance.fields.reverse()
                stack.push(instance.fields)
                stack.pushReference(exceptionRef)
                cstack.push(
                    breakDispatcher(ControlInstruction.Br(catchHandler.labelIndex)),
                )
            }
            catchHandler is CatchHandler.CatchAll -> {
                cstack.push(
                    breakDispatcher(ControlInstruction.Br(catchHandler.labelIndex)),
                )
            }
            catchHandler is CatchHandler.CatchAllRef -> {
                stack.pushReference(exceptionRef)
                cstack.push(
                    breakDispatcher(ControlInstruction.Br(catchHandler.labelIndex)),
                )
            }
            else -> {

                handler.instructions = otherHandlers
                cstack.push(handler)
                val instruction = handlerDispatcher(handler)
                cstack.push(instruction)
                stack.pushReference(exceptionRef)
                cstack.push(ThrowRefDispatcher(ControlInstruction.ThrowRef))
            }
        }
    }
}

private inline fun jumpToHandlerInstruction(controlStack: ControlStack): ExceptionHandler {

    val handler = controlStack.popHandler()

    controlStack.shrinkLabels(0, handler.labelsDepth)
    controlStack.shrinkFrames(0, handler.framesDepth)
    controlStack.shrinkInstructions(0, handler.instructionsDepth)

    return handler
}
