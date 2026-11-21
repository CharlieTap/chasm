package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.dex.analysis.SlotKindFlow

internal class MissingHome(
    slot: Int,
    kind: Byte,
) : HomeMismatch("slot $slot has no home of kind $kind")

internal class Registers(
    frameSlots: Int,
    temporaries: Int,
    windowWords: Int,
    val parameterWords: Int,
    cachedConstants: Int,
    unsafeMemory: Boolean,
    inlineMemory: Boolean,
    savesStack: Boolean = false,
    callBuffer: Boolean,
    private val homes: RegisterHomes? = null,
    private val held: ByteArray? = null,
    private val recording: ByteArray? = null,
    chunk: Boolean = false,
    private val constantsUsed: BooleanArray? = null,
) {
    private var next = SCRATCH

    val binding = next++

    val frameBase = if (chunk) next++ else UNUSED

    val objectFrame = if (chunk) next++ else UNUSED

    val memorySize = if (inlineMemory) next++ else UNUSED

    val unsafe = if (unsafeMemory) next else UNUSED

    val memoryBytes = when {
        unsafeMemory -> unsafe + 1
        inlineMemory -> next++
        else -> UNUSED
    }

    val unsafeOffset = unsafe + 2
    val unsafeValue = unsafe + 4

    val byteArrayBase = if (unsafeMemory) unsafe + UNSAFE_WINDOW else UNUSED

    private val homeBase = if (unsafeMemory) next + UNSAFE_WINDOW + 2 else next

    private val homeOffsets = IntArray(frameSlots * HOME_KINDS + 1).also { offsets ->
        for (slot in 0 until frameSlots) {

            val mask = heldMask(slot)

            for (kind in SlotKindFlow.LONG..SlotKindFlow.OBJECT) {
                val index = slot * HOME_KINDS + SlotKindFlow.homeIndex(kind.toByte())
                val width = when {
                    mask and SlotKindFlow.homeBit(kind.toByte()) == 0 -> 0
                    SlotKindFlow.wide(kind.toByte()) -> 2
                    else -> 1
                }
                offsets[index + 1] = offsets[index] + width
            }
        }
    }

    val records: Boolean get() = recording != null

    fun heldMask(slot: Int): Int {
        val homes = homes
        val mask = if (homes == null) SlotKindFlow.homeBit(SlotKindFlow.LONG) else homes.mask(slot)
        return if (held == null) mask else mask and held[slot].toInt()
    }

    fun holds(slot: Int): Boolean {
        return heldMask(slot) != 0
    }

    fun holds(
        slot: Int,
        kind: Byte,
    ): Boolean {
        return heldMask(slot) and SlotKindFlow.homeBit(kind) != 0
    }

    fun home(
        slot: Int,
        kind: Byte,
    ): Int {

        if (recording != null) {
            recording[slot] = (recording[slot].toInt() or SlotKindFlow.homeBit(kind)).toByte()
        }
        if (!holds(slot, kind)) {
            throw MissingHome(slot, kind)
        }

        return homeBase + homeOffsets[slot * HOME_KINDS + SlotKindFlow.homeIndex(kind)]
    }

    private val temporaryBase = homeBase + homeOffsets[frameSlots * HOME_KINDS]

    fun temporary(
        index: Int,
    ) = temporaryBase + 2 * index

    val savedStack = if (savesStack) temporaryBase + 2 * temporaries else UNUSED

    private val pairsEnd = temporaryBase + 2 * temporaries + (if (savesStack) 2 else 0)

    val callBuffer = if (callBuffer) pairsEnd else UNUSED

    private val cacheBase = pairsEnd + if (callBuffer) 1 else 0

    fun cachedConstant(
        index: Int,
    ) = cacheBase + index

    fun useCachedConstant(
        index: Int,
    ): Int {
        constantsUsed?.set(index, true)
        return cachedConstant(index)
    }

    val window = cacheBase + cachedConstants

    private val parameterBase = window + windowWords

    val count = parameterBase + 3 + parameterWords

    val bindingParameter = parameterBase

    val contextParameter = parameterBase + 1

    val depthParameter = parameterBase + 2

    fun parameter(
        offset: Int,
    ) = parameterBase + 3 + offset

    companion object {
        const val W0 = 0
        const val W1 = 2
        const val W2 = 4
        const val W3 = 6
        const val I0 = 8
        const val I1 = 9
        const val I2 = 10
        const val I3 = 11

        const val OBJECT = 12
        const val SECOND_OBJECT = I2
        const val THIRD_OBJECT = I3
        const val DEPTH = 13
        const val CONTEXT = 14

        const val NARROW = 15

        private const val SCRATCH = 16
        private const val HOME_KINDS = 5
        private const val UNSAFE_WINDOW = 6
        private const val UNUSED = Int.MIN_VALUE / 2
    }
}
