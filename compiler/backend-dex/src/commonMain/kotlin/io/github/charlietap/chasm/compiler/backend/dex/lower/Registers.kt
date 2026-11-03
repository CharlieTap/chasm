package io.github.charlietap.chasm.compiler.backend.dex.lower

internal class Registers(
    frameSlots: Int,
    temporaries: Int,
    windowWords: Int,
    val parameterWords: Int,
    cachedConstants: Int,
    unsafeMemory: Boolean,
    inlineMemory: Boolean,
    activation: Boolean,
    savesStack: Boolean,
    callBuffer: Boolean,
    narrowHomes: BooleanArray? = null,
    private val held: BooleanArray? = null,
    private val recording: BooleanArray? = null,
    chunk: Boolean = false,
    private val constantsUsed: BooleanArray? = null,
) {
    private var next = SCRATCH

    val binding = next++

    val frameBase = if (chunk) next++ else UNUSED

    val memorySize = if (inlineMemory) next++ else UNUSED

    val activationArray = if (activation) next++ else UNUSED
    val activationFp = if (activation) next++ else UNUSED
    val activationIndex = if (activation) next++ else UNUSED

    val unsafe = if (unsafeMemory) next else UNUSED

    val memoryBytes = when {
        unsafeMemory -> unsafe + 1
        inlineMemory -> next++
        else -> UNUSED
    }

    val unsafeOffset = unsafe + 2
    val unsafeValue = unsafe + 4

    val byteArrayBase = if (unsafeMemory) unsafe + UNSAFE_WINDOW else UNUSED

    private val homes = if (unsafeMemory) next + UNSAFE_WINDOW + 2 else next

    private val homeOffsets = IntArray(frameSlots + 1).also { offsets ->
        for (slot in 0 until frameSlots) {
            val width = when {
                held != null && !held[slot] -> 0
                narrowHomes?.get(slot) == true -> 1
                else -> 2
            }
            offsets[slot + 1] = offsets[slot] + width
        }
    }

    val records: Boolean get() = recording != null

    fun holds(
        slot: Int,
    ): Boolean = held == null || held[slot]

    fun home(
        slot: Int,
    ): Int {
        recording?.set(slot, true)
        check(holds(slot)) { "slot $slot is not held" }
        return homes + homeOffsets[slot]
    }

    private val temporaryBase = homes + homeOffsets[frameSlots]

    fun temporary(
        index: Int,
    ) = temporaryBase + 2 * index

    val savedStack = if (savesStack) temporaryBase + 2 * temporaries else UNUSED

    val activationSaved = if (activation) temporaryBase + 2 * temporaries + (if (savesStack) 2 else 0) else UNUSED

    private val pairsEnd = temporaryBase + 2 * temporaries + (if (savesStack) 2 else 0) + (if (activation) 2 else 0)

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
        const val DEPTH = 13
        const val CONTEXT = 14

        const val NARROW = 15

        private const val SCRATCH = 16
        private const val UNSAFE_WINDOW = 6
        private const val UNUSED = Int.MIN_VALUE / 2
    }
}
