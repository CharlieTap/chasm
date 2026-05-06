package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowRefDispatcher
import io.github.charlietap.chasm.executor.invoker.ext.tagAddress
import io.github.charlietap.chasm.ir.instruction.ControlInstruction.CatchHandler
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.exception
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.toExceptionAddress
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal fun ThrowRefExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.ThrowRef,
) {
    val throwRefDispatcher: Dispatcher<ControlInstruction.ThrowRef> = ::ThrowRefDispatcher
    ThrowRefExecutor(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        instruction = instruction,
        breakDispatcher = ::BrDispatcher,
        throwRefDispatcher = throwRefDispatcher,
    )
}

internal inline fun ThrowRefExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.ThrowRef,
    crossinline breakDispatcher: Dispatcher<ControlInstruction.Br>,
    crossinline throwRefDispatcher: Dispatcher<ControlInstruction.ThrowRef>,
) {
    ThrowRefValueExecutor(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        ref = vstack.pop(),
        breakDispatcher = breakDispatcher,
        throwRefDispatcher = throwRefDispatcher,
    )
}

internal inline fun ThrowRefValueExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    ref: Long,
    crossinline breakDispatcher: Dispatcher<ControlInstruction.Br>,
    crossinline throwRefDispatcher: Dispatcher<ControlInstruction.ThrowRef>,
) {
    val exceptionAddress = if (ref.isNullableReference()) {
        throw InvocationException(InvocationError.UnexpectedReferenceValue)
    } else {
        ref.toExceptionAddress()
    }

    val instance = store.exception(exceptionAddress)
    val address = instance.tagAddress

    val handler = jumpToHandlerInstruction(cstack, vstack)

    if (handler.instructions.isEmpty()) {
        vstack.push(ReferenceValue.Exception(exceptionAddress).toLong())
        cstack.push(ThrowRefDispatcher(ControlInstruction.ThrowRef))
    } else {

        val frame = cstack.peekFrame()

        val catchHandler = handler.instructions.first()
        val otherHandlers = handler.instructions.drop(1)
        val payloadDestinationSlots = handler.payloadDestinationSlots.firstOrNull() ?: emptyList()
        val continuation = handler.continuations.firstOrNull()
        val continuationOffset = handler.continuationOffsets.firstOrNull() ?: -1

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
                if (frame.frameSlotMode) {
                    writeCatchPayloadToSlots(vstack, instance.fields, payloadDestinationSlots)
                } else {
                    vstack.push(instance.fields)
                }
                routeExceptionHandlerMatch(cstack, continuation, handler.continuationSource, continuationOffset, breakDispatcher, catchHandler.labelIndex)
            }
            catchHandler is CatchHandler.CatchRef && tagMatches -> {
                instance.fields.reverse()
                if (frame.frameSlotMode) {
                    writeCatchRefPayloadToSlots(vstack, instance.fields, ref, payloadDestinationSlots)
                } else {
                    vstack.push(instance.fields)
                    vstack.push(ref)
                }
                routeExceptionHandlerMatch(cstack, continuation, handler.continuationSource, continuationOffset, breakDispatcher, catchHandler.labelIndex)
            }
            catchHandler is CatchHandler.CatchAll -> {
                routeExceptionHandlerMatch(cstack, continuation, handler.continuationSource, continuationOffset, breakDispatcher, catchHandler.labelIndex)
            }
            catchHandler is CatchHandler.CatchAllRef -> {
                if (frame.frameSlotMode) {
                    vstack.setFrameSlot(payloadDestinationSlots.single(), ref)
                } else {
                    vstack.push(ref)
                }
                routeExceptionHandlerMatch(cstack, continuation, handler.continuationSource, continuationOffset, breakDispatcher, catchHandler.labelIndex)
            }
            else -> {

                handler.instructions = otherHandlers
                handler.payloadDestinationSlots = handler.payloadDestinationSlots.drop(1)
                handler.continuations = handler.continuations.drop(1)
                handler.continuationOffsets = handler.continuationOffsets.drop(1)
                cstack.push(handler)
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
    controlStack.shrinkInstructions(handler.instructionsDepth)
    valueStack.framePointer = handler.framePointer

    return handler
}

private fun writeCatchPayloadToSlots(
    vstack: ValueStack,
    fields: LongArray,
    payloadDestinationSlots: List<Int>,
) {
    fields.forEachIndexed { index, value ->
        vstack.setFrameSlot(payloadDestinationSlots[index], value)
    }
}

private fun writeCatchRefPayloadToSlots(
    vstack: ValueStack,
    fields: LongArray,
    ref: Long,
    payloadDestinationSlots: List<Int>,
) {
    writeCatchPayloadToSlots(vstack, fields, payloadDestinationSlots)
    vstack.setFrameSlot(payloadDestinationSlots[fields.size], ref)
}

private inline fun routeExceptionHandlerMatch(
    cstack: ControlStack,
    continuation: Array<io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction>?,
    continuationSource: List<io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction?>?,
    continuationOffset: Int,
    breakDispatcher: Dispatcher<ControlInstruction.Br>,
    labelIndex: io.github.charlietap.chasm.ir.module.Index.LabelIndex,
) {
    if (continuationSource != null) {
        cstack.pushContinuation(continuationSource, continuationOffset)
    } else if (continuation != null) {
        if (continuation.isNotEmpty()) {
            cstack.push(continuation)
        }
    } else {
        cstack.push(
            breakDispatcher(ControlInstruction.Br(labelIndex)),
        )
    }
}
