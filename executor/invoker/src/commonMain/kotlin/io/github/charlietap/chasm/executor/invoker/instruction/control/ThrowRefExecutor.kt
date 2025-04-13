package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowRefDispatcher
import io.github.charlietap.chasm.executor.invoker.ext.tagAddress
import io.github.charlietap.chasm.ir.instruction.ControlInstruction.CatchHandler
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.exception
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.toExceptionAddress
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal inline fun ThrowRefExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.ThrowRef,
): InstructionPointer {
    val ref = vstack.pop()

    val exceptionAddress = if (ref.isNullableReference()) {
        throw InvocationException(InvocationError.UnexpectedReferenceValue)
    } else {
        ref.toExceptionAddress()
    }

    val instance = store.exception(exceptionAddress)
    val address = instance.tagAddress

    val handler = jumpToHandlerInstruction(cstack, vstack)

    if (handler.handlers.isEmpty()) {
        vstack.push(ReferenceValue.Exception(exceptionAddress).toLong())
        cstack.push(ThrowRefDispatcher(ControlInstruction.ThrowRef))
    } else {

        val frame = cstack.peekFrame()

        val catchHandler = handler.handlers.first()
        val otherHandlers = handler.handlers.drop(1)

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
                vstack.push(instance.fields)
                cstack.push(
                    breakDispatcher(ControlInstruction.Br(catchHandler.labelIndex)),
                )
            }
            catchHandler is CatchHandler.CatchRef && tagMatches -> {
                instance.fields.reverse()
                vstack.push(instance.fields)
                vstack.push(ref)
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
                vstack.push(ref)
                cstack.push(
                    breakDispatcher(ControlInstruction.Br(catchHandler.labelIndex)),
                )
            }
            else -> {

                handler.handlers = otherHandlers
                cstack.push(handler)
                val instruction = handlerDispatcher(handler)
                cstack.push(instruction)
                vstack.push(ref)
                cstack.push(ThrowRefDispatcher(ControlInstruction.ThrowRef))
            }
        }
    }
}

private inline fun jumpToHandlerInstruction(
    controlStack: ControlStack,
    valueStack: ValueStack,
): ExceptionHandler {

    val handler = controlStack.popHandler()

    controlStack.shrinkLabels(handler.labelsDepth)
    controlStack.shrinkFrames(handler.framesDepth)
    controlStack.shrinkInstructions(handler.handlersDepth)
    valueStack.framePointer = handler.framePointer

    return handler
}
