package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.ast.instruction.ControlInstruction.CatchHandler
import io.github.charlietap.chasm.executor.invoker.ext.tagAddress
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun ThrowRefExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    instruction: ControlInstruction.ThrowRef,
): Int = ThrowRefValueExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    ref = vstack.pop(),
)

internal fun ThrowRefValueExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    ref: Long,
): Int {
    if (ref.isNullableReference()) {
        throw InvocationException(InvocationError.UnexpectedReferenceValue)
    }
    val exceptionTagAddress = store.heap.exceptionTagAddress(ref)
    while (true) {
        val handler = cstack.popHandler()

        cstack.shrinkFrames(handler.framesDepth)
        vstack.framePointer = handler.framePointer
        vstack.shrink(0, handler.valueDepth)

        val moduleInstance = cstack.frameInstance()
        handler.handlers.forEachIndexed { index, catchHandler ->
            val tagMatches = when (catchHandler) {
                is CatchHandler.Catch -> {
                    exceptionTagAddress == moduleInstance.tagAddress(catchHandler.tagIndex)
                }
                is CatchHandler.CatchRef -> {
                    exceptionTagAddress == moduleInstance.tagAddress(catchHandler.tagIndex)
                }
                else -> true
            }

            if (tagMatches) {
                val destinationSlots = handler.payloadDestinationSlots[index]
                when (catchHandler) {
                    is CatchHandler.Catch -> {
                        writeCatchPayload(vstack, store, ref, destinationSlots.size, destinationSlots)
                    }
                    is CatchHandler.CatchRef -> {
                        writeCatchPayload(vstack, store, ref, destinationSlots.lastIndex, destinationSlots)
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
    store: Store,
    exceptionReference: Long,
    payloadCount: Int,
    destinationSlots: IntArray,
) {
    var fieldIndex = 0
    while (fieldIndex < payloadCount) {
        vstack.setFrameSlot(
            destinationSlots[fieldIndex],
            store.heap.getExceptionFieldTrusted(exceptionReference, fieldIndex),
        )
        fieldIndex++
    }
}
