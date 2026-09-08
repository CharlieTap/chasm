package io.github.charlietap.chasm.fixture.runtime.exception

import io.github.charlietap.chasm.runtime.exception.CompiledCatch
import io.github.charlietap.chasm.runtime.exception.ExceptionRegion
import io.github.charlietap.chasm.runtime.exception.FunctionExceptionTable

fun functionExceptionTable(
    entryIp: Int = 0,
    instructionCount: Int = 1,
    interfaceSlotCount: Int = 0,
    resultCount: Int = 0,
    regions: Array<ExceptionRegion> = emptyArray(),
    tailCallOffsets: IntArray = intArrayOf(),
) = FunctionExceptionTable(
    entryIp = entryIp,
    instructionCount = instructionCount,
    interfaceSlotCount = interfaceSlotCount,
    resultCount = resultCount,
    regions = regions,
    tailCallOffsets = tailCallOffsets,
)

fun exceptionRegion(
    startOffset: Int = 0,
    endOffset: Int = 1,
    parentRegion: Int = -1,
    catches: Array<CompiledCatch> = emptyArray(),
) = ExceptionRegion(
    startOffset = startOffset,
    endOffset = endOffset,
    parentRegion = parentRegion,
    catches = catches,
)

fun compiledCatch(
    tagAddress: Int = CompiledCatch.CATCH_ALL_TAG,
    targetOffset: Int = 0,
    payloadSlots: IntArray = intArrayOf(),
    includeExceptionReference: Boolean = false,
    stackSlotCount: Int = 1,
) = CompiledCatch(
    tagAddress = tagAddress,
    targetOffset = targetOffset,
    payloadSlots = payloadSlots,
    includeExceptionReference = includeExceptionReference,
    stackSlotCount = stackSlotCount,
)
