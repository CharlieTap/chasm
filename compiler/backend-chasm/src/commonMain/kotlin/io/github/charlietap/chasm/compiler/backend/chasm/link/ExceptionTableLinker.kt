package io.github.charlietap.chasm.compiler.backend.chasm.link

import io.github.charlietap.chasm.cir.ExceptionTable
import io.github.charlietap.chasm.runtime.exception.CompiledCatch
import io.github.charlietap.chasm.runtime.exception.ExceptionRegion
import io.github.charlietap.chasm.runtime.exception.FunctionExceptionTable

internal typealias ExceptionTableLinker = (ExceptionTable, LinkContext, Int, Int, Int) -> FunctionExceptionTable

internal fun ExceptionTableLinker(
    table: ExceptionTable,
    context: LinkContext,
    instructionCount: Int,
    interfaceSlotCount: Int,
    resultCount: Int,
): FunctionExceptionTable {
    val entryIp = context.entryIp
    val tailCalls = table.tailCalls
    return FunctionExceptionTable(
        entryIp = entryIp,
        instructionCount = instructionCount,
        interfaceSlotCount = interfaceSlotCount,
        resultCount = resultCount,
        regions = Array(table.regions.size) { index ->
            val region = table.regions[index]
            ExceptionRegion(
                context.ip(region.start) - entryIp,
                context.ip(region.end) - entryIp,
                region.parent ?: -1,
                Array(region.catches.size) { catchIndex ->
                    val handler = region.catches[catchIndex]
                    CompiledCatch(
                        handler.tag?.let { tag -> context.instance.tagAddresses[tag.index].address } ?: CompiledCatch.CATCH_ALL_TAG,
                        context.ip(handler.target) - entryIp,
                        handler.payloadSlots,
                        handler.exceptionReference,
                        handler.stackSlots,
                    )
                },
            )
        },
        tailCallOffsets = IntArray(tailCalls.size) { index -> context.ip(tailCalls[index]) - entryIp },
    )
}
