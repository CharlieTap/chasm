package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.RTT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.descriptorWords
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKindFlow
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKinds
import org.objectweb.asm.Opcodes

internal class Locals(
    binding: String,
    arguments: List<String>,
    private val slotKinds: SlotKinds,
    homes: SlotKindFlow,
    frameSlots: Int,
    slots: IntArray = IntArray(frameSlots) { it },
    private val recording: BooleanArray? = null,
    val inlineMemory: InlineMemory? = null,
) {
    val argumentLocals = IntArray(arguments.size)

    private val slotLocals = IntArray(frameSlots) { -1 }

    private val objectLocals = IntArray(frameSlots) { -1 }

    private val floatShadows = IntArray(frameSlots) { -1 }
    private val doubleShadows = IntArray(frameSlots) { -1 }

    val scratchLong = IntArray(SCRATCH_LONGS)
    val scratchInt: Int
    val scratchValues: Int
    val scratchReferences: Int

    val scratchValue: Int
    val scratchObject: Int
    val scratchRtt: Int

    private val scratchArrays = HashMap<String, Int>()

    val frameArray: Int
    val objectFrame: Int

    val frameSaved: Int
    val frameBase: Int

    val exitPosition: Int

    val stackSaved: Int

    val memoryBytes: Int
    val memoryBuffer: Int
    val memorySize: Int
    val unsafe: Int
    val byteArrayBase: Int

    val size: Int

    val firstLocalAfterParameters: Int

    val frameTypes: Array<Any>

    val heldSlots: IntArray = slots

    val isRecording: Boolean get() = recording != null

    val holdsObjects: Boolean = (0 until frameSlots).any { slot -> homes.homeMask(slot) and OBJECT_HOME != 0 }

    init {
        val types = ArrayList<Any>()
        var next = 0

        fun local(
            type: Any,
            words: Int,
        ): Int {
            val index = next
            types += type
            next += words
            return index
        }
        local(internalName(binding), 1)
        local(internalName(CONTEXT_TYPE), 1)
        local(Opcodes.INTEGER, 1)
        arguments.forEachIndexed { index, descriptor -> argumentLocals[index] = local(frameType(descriptor), descriptorWords(descriptor)) }
        firstLocalAfterParameters = next
        for (slot in slots) {
            val mask = homes.homeMask(slot)
            if (mask and OBJECT_HOME.inv() != 0 || mask == 0) {
                val kind = slotKinds.kind(slot)
                slotLocals[slot] = local(kindFrameType(kind), if (kind == SlotKinds.LONG || kind == SlotKinds.DOUBLE) 2 else 1)
                if (slotKinds.floatShadow(slot)) floatShadows[slot] = local(Opcodes.FLOAT, 1)
                if (slotKinds.doubleShadow(slot)) doubleShadows[slot] = local(Opcodes.DOUBLE, 2)
            }
            if (mask and OBJECT_HOME != 0) {
                objectLocals[slot] = local(internalName(OBJECT_TYPE), 1)
            }
        }
        for (index in 0 until SCRATCH_LONGS) scratchLong[index] = local(Opcodes.LONG, 2)
        scratchInt = local(Opcodes.INTEGER, 1)
        scratchValue = local(Opcodes.INTEGER, 1)
        scratchValues = local("[J", 1)
        scratchReferences = local(OBJECT_ARRAY, 1)
        scratchObject = local(internalName(OBJECT_TYPE), 1)
        scratchRtt = local(internalName(RTT_TYPE), 1)
        STORAGE_ARRAYS.forEach { descriptor -> scratchArrays[descriptor] = local(descriptor, 1) }
        frameArray = local("[J", 1)
        objectFrame = local(OBJECT_ARRAY, 1)
        frameSaved = local(Opcodes.LONG, 2)
        frameBase = local(Opcodes.INTEGER, 1)
        exitPosition = local(Opcodes.INTEGER, 1)
        stackSaved = local(Opcodes.LONG, 2)
        memoryBytes = if (inlineMemory == InlineMemory.BYTES || inlineMemory == InlineMemory.UNSAFE) local("[B", 1) else -1
        memoryBuffer = if (inlineMemory == InlineMemory.BUFFER) local("java/nio/ByteBuffer", 1) else -1
        memorySize = if (inlineMemory != null) local(Opcodes.INTEGER, 1) else -1
        unsafe = if (inlineMemory == InlineMemory.UNSAFE) local("sun/misc/Unsafe", 1) else -1
        byteArrayBase = if (inlineMemory == InlineMemory.UNSAFE) local(Opcodes.LONG, 2) else -1
        size = next
        frameTypes = types.toTypedArray()
    }

    val parameterLocals: IntArray get() = argumentLocals

    val storageArrayScratches: Collection<Int> get() = scratchArrays.values

    fun slot(
        slot: Int,
    ): Int {
        recording?.set(slot, true)
        val local = slotLocals[slot]
        check(local >= 0) { "slot $slot has no number local here" }
        return local
    }

    fun objectSlot(
        slot: Int,
    ): Int {
        recording?.set(slot, true)
        val local = objectLocals[slot]
        check(local >= 0) { "slot $slot has no object local here" }
        return local
    }

    fun holdsNumber(slot: Int): Boolean {
        return slotLocals[slot] >= 0
    }

    fun holdsObject(slot: Int): Boolean {
        return objectLocals[slot] >= 0
    }

    fun kind(
        slot: Int,
    ): Byte = slotKinds.kind(slot)

    fun floatShadow(
        slot: Int,
    ): Int {
        recording?.set(slot, true)
        return floatShadows[slot]
    }

    fun doubleShadow(
        slot: Int,
    ): Int {
        recording?.set(slot, true)
        return doubleShadows[slot]
    }

    fun storageArrayScratch(
        descriptor: String,
    ): Int = scratchArrays.getValue(descriptor)

    fun slotOf(
        local: Int,
    ): Int = localSlots.getOrElse(local) { -1 }

    private val localSlots: IntArray by lazy {
        IntArray(size) { -1 }.also { owners ->
            for (slot in slotLocals.indices) {
                listOf(slotLocals[slot], objectLocals[slot], floatShadows[slot], doubleShadows[slot]).forEach { local ->
                    if (local >= 0) owners[local] = slot
                }
            }
        }
    }

    companion object {
        const val BINDING = 0
        const val CONTEXT = 1
        const val DEPTH = 2
        private const val SCRATCH_LONGS = 4
        private const val OBJECT_ARRAY = "[Ljava/lang/Object;"
        private val OBJECT_HOME = SlotKindFlow.homeBit(SlotKindFlow.OBJECT)
        private val STORAGE_ARRAYS = listOf("[B", "[C", "[I", "[J", "[F", "[D", OBJECT_ARRAY)

        private fun frameType(
            descriptor: String,
        ): Any = when (descriptor) {
            "I" -> Opcodes.INTEGER
            "J" -> Opcodes.LONG
            "F" -> Opcodes.FLOAT
            "D" -> Opcodes.DOUBLE
            else -> internalName(descriptor)
        }

        private fun kindFrameType(
            kind: Byte,
        ): Any = when (kind) {
            SlotKinds.INT -> Opcodes.INTEGER
            SlotKinds.FLOAT -> Opcodes.FLOAT
            SlotKinds.DOUBLE -> Opcodes.DOUBLE
            else -> Opcodes.LONG
        }
    }
}
