package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.ast.instruction.ControlInstruction.CatchHandler
import io.github.charlietap.chasm.executor.invoker.ext.tagAddress
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.exception
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.toExceptionAddress
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
    val exceptionAddress = if (ref.isNullableReference()) {
        throw InvocationException(InvocationError.UnexpectedReferenceValue)
    } else {
        ref.toExceptionAddress()
    }
    val exception = store.exception(exceptionAddress)

    while (true) {
        val handler = cstack.popHandler()

        cstack.shrinkFrames(handler.framesDepth)
        vstack.framePointer = handler.framePointer
        vstack.shrink(0, handler.valueDepth)

        val moduleInstance = cstack.frameInstance()
        handler.handlers.forEachIndexed { index, catchHandler ->
            val tagMatches = when (catchHandler) {
                is CatchHandler.Catch -> exception.tagAddress == moduleInstance.tagAddress(catchHandler.tagIndex)
                is CatchHandler.CatchRef -> exception.tagAddress == moduleInstance.tagAddress(catchHandler.tagIndex)
                else -> true
            }

            if (tagMatches) {
                val destinationSlots = handler.payloadDestinationSlots[index]
                when (catchHandler) {
                    is CatchHandler.Catch -> writeCatchPayload(vstack, exception.fields, destinationSlots)
                    is CatchHandler.CatchRef -> {
                        writeCatchPayload(vstack, exception.fields, destinationSlots)
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
    fields: LongArray,
    destinationSlots: IntArray,
) {
    for (index in fields.indices) {
        vstack.setFrameSlot(destinationSlots[fields.lastIndex - index], fields[index])
    }
}
