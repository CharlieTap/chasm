package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.ast.instruction.ControlInstruction.CatchHandler
import io.github.charlietap.chasm.executor.invoker.ext.tagAddress
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun ThrowRefValueExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    ref: Long,
): Int {
    if (ref.isNullableReference()) {
        throw InvocationException(InvocationError.UnexpectedReferenceValue)
    }
    val cstack = context.cstack
    val heap = context.heap
    val exceptionTagAddress = heap.exceptionTagAddress(ref)
    while (true) {
        if (cstack.handlersDepth() == 0) {
            heap.setPendingException(ref)
            throw InvocationException(InvocationError.ThrownException)
        }
        val handler = cstack.popHandler()

        vstack.fp = handler.fp
        vstack.shrink(0, handler.sp)

        handler.handlers.forEachIndexed { index, catchHandler ->
            val tagMatches = when (catchHandler) {
                is CatchHandler.Catch -> {
                    exceptionTagAddress == handler.instance.tagAddress(catchHandler.tagIndex)
                }
                is CatchHandler.CatchRef -> {
                    exceptionTagAddress == handler.instance.tagAddress(catchHandler.tagIndex)
                }
                else -> true
            }

            if (tagMatches) {
                val destinationSlots = handler.payloadDestinationSlots[index]
                when (catchHandler) {
                    is CatchHandler.Catch -> {
                        writeCatchPayload(vstack, heap, ref, destinationSlots.size, destinationSlots)
                    }
                    is CatchHandler.CatchRef -> {
                        writeCatchPayload(vstack, heap, ref, destinationSlots.lastIndex, destinationSlots)
                        vstack.setFrameSlot(destinationSlots.last(), ref)
                    }
                    is CatchHandler.CatchAll -> Unit
                    is CatchHandler.CatchAllRef -> vstack.setFrameSlot(destinationSlots.single(), ref)
                }
                return handler.continuationIps[index]
            }
        }
    }
}

private fun writeCatchPayload(
    vstack: ValueStack,
    heap: WasmHeap,
    exceptionReference: Long,
    payloadCount: Int,
    destinationSlots: IntArray,
) {
    var fieldIndex = 0
    while (fieldIndex < payloadCount) {
        vstack.setFrameSlot(
            destinationSlots[fieldIndex],
            heap.getExceptionFieldTrusted(exceptionReference, fieldIndex),
        )
        fieldIndex++
    }
}
