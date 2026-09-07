package io.github.charlietap.chasm.runtime.exception

class FunctionExceptionTable(
    val entryIp: Int,
    val instructionCount: Int,
    val interfaceSlotCount: Int,
    val resultCount: Int,
    val regions: Array<ExceptionRegion>,
    val tailCallOffsets: IntArray,
) {
    init {
        require(entryIp >= 0 && instructionCount > 0 && instructionCount <= Int.MAX_VALUE - entryIp)
        require(interfaceSlotCount >= 0 && resultCount in 0..interfaceSlotCount)
        for (index in regions.indices) {
            val region = regions[index]
            require(region.startOffset in 0..region.endOffset && region.endOffset <= instructionCount)
            require(region.parentRegion in -1 until index)
            if (region.parentRegion >= 0) {
                val parent = regions[region.parentRegion]
                require(region.startOffset >= parent.startOffset && region.endOffset <= parent.endOffset)
            }
            for (handler in region.catches) {
                require(handler.tagAddress >= CompiledCatch.CATCH_ALL_TAG)
                require(handler.targetOffset in 0 until instructionCount)
                require(handler.stackSlotCount > interfaceSlotCount)
                require(!handler.includeExceptionReference || handler.payloadSlots.isNotEmpty())
                require(handler.payloadSlots.all { it in 0 until handler.stackSlotCount })
            }
        }
        for (index in tailCallOffsets.indices) {
            require(tailCallOffsets[index] in 0 until instructionCount)
            require(index == 0 || tailCallOffsets[index - 1] < tailCallOffsets[index])
        }
    }

    val endIp: Int
        get() = entryIp + instructionCount

    fun relocated(entryIp: Int): FunctionExceptionTable = FunctionExceptionTable(
        entryIp,
        instructionCount,
        interfaceSlotCount,
        resultCount,
        regions,
        tailCallOffsets,
    )

    fun excludesTailCall(offset: Int): Boolean {
        var low = 0
        var high = tailCallOffsets.lastIndex
        while (low <= high) {
            val middle = (low + high) ushr 1
            val candidate = tailCallOffsets[middle]
            when {
                candidate < offset -> low = middle + 1
                candidate > offset -> high = middle - 1
                else -> return true
            }
        }
        return false
    }

    fun innermostRegion(offset: Int): Int {
        var index = regions.lastIndex
        while (index >= 0) {
            val region = regions[index]
            if (offset >= region.startOffset && offset < region.endOffset) return index
            index--
        }
        return -1
    }
}

class ExceptionRegion(
    val startOffset: Int,
    val endOffset: Int,
    val parentRegion: Int,
    val catches: Array<CompiledCatch>,
)

class CompiledCatch(
    val tagAddress: Int,
    val targetOffset: Int,
    val payloadSlots: IntArray,
    val includeExceptionReference: Boolean,
    val stackSlotCount: Int,
) {
    companion object {
        const val CATCH_ALL_TAG = -1
    }
}
