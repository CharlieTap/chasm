package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.function.exceptionalCallSiteIp
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.CompiledCatch
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.program.EXIT_IP
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun ThrowRefValueExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    ref: Long,
    faultIp: Int,
): Int {
    if (ref.isNullableReference()) {
        throw InvocationException(InvocationError.UnexpectedReferenceValue)
    }
    val heap = context.heap
    val program = context.store.program
    if (!program.hasExceptionHandlers) escapeException(heap, ref)
    val tagAddress = heap.exceptionTagAddress(ref).address
    var ip = faultIp
    var previousIp = -1
    var interfaceSlotCount = 0
    var resultCount = 0
    while (true) {
        if (ip !in 0 until program.size) {
            throw InvocationException(InvocationError.ProgramFinishedInconsistentState)
        }
        // Reuse a miss when recursive frames share the same call site.
        if (ip != previousIp) {
            val table = program.exceptionTable(ip)
            if (table != null) {
                val offset = ip - table.entryIp
                var regionIndex = if (table.excludesTailCall(offset)) -1 else table.innermostRegion(offset)
                while (regionIndex >= 0) {
                    val region = table.regions[regionIndex]
                    for (handler in region.catches) {
                        if (handler.tagAddress == CompiledCatch.CATCH_ALL_TAG || handler.tagAddress == tagAddress) {
                            // No guest allocation occurs before the catch payload is rooted.
                            vstack.activateFrameAtDepth(vstack.fp, vstack.fp + handler.stackSlotCount)
                            val slots = handler.payloadSlots
                            val payloadCount = slots.size - if (handler.includeExceptionReference) 1 else 0
                            if (handler.tagAddress != CompiledCatch.CATCH_ALL_TAG) {
                                writeCatchPayload(vstack, heap, ref, payloadCount, slots)
                            }
                            if (handler.includeExceptionReference) vstack.setFrameSlot(slots.last(), ref)
                            return table.entryIp + handler.targetOffset
                        }
                    }
                    regionIndex = region.parentRegion
                }
                interfaceSlotCount = table.interfaceSlotCount
                resultCount = table.resultCount
            } else {
                val function = findOwningFunction(context.store, ip)
                interfaceSlotCount = function.callStrategy.interfaceSlotCount
                resultCount = function.functionType.results.types.size
            }
            previousIp = ip
        }
        val returnIp = vstack.unwindCallerFrame(interfaceSlotCount)
        if (returnIp == EXIT_IP) escapeException(heap, ref)
        ip = exceptionalCallSiteIp(returnIp, resultCount)
    }
}

private fun findOwningFunction(store: Store, ip: Int): FunctionInstance.WasmFunction {
    var owner: FunctionInstance.WasmFunction? = null
    var closestEntry = -1
    var index = 0
    while (index < store.functions.size) {
        val function = store.functions[index]
        if (function is FunctionInstance.WasmFunction) {
            val entry = function.callStrategy.entryIp
            if (entry in 0..ip && entry > closestEntry) {
                owner = function
                closestEntry = entry
            }
        }
        index++
    }
    return owner ?: throw InvocationException(InvocationError.ProgramFinishedInconsistentState)
}

private fun escapeException(heap: WasmHeap, ref: Long): Nothing {
    heap.setPendingException(ref)
    throw InvocationException(InvocationError.ThrownException)
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
