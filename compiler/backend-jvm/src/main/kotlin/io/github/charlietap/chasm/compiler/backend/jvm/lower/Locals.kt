package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.descriptorWords
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKinds
import org.objectweb.asm.Opcodes

internal class Locals(
    binding: String,
    arguments: List<String>,
    private val slotKinds: SlotKinds,
    frameSlots: Int,
    slots: IntArray = IntArray(frameSlots) { it },
    private val recording: BooleanArray? = null,
    val inlineMemory: InlineMemory? = null,
) {
    val argumentLocals = IntArray(arguments.size)

    private val slotLocals = IntArray(frameSlots) { -1 }

    private val floatShadows = IntArray(frameSlots) { -1 }
    private val doubleShadows = IntArray(frameSlots) { -1 }

    val scratchLong = IntArray(SCRATCH_LONGS)
    val scratchInt: Int
    val scratchValues: Int

    val scratchValue: Int
    val scratchObject: Int

    val rootsSaved: Int
    val rootBase: Int

    val exitPosition: Int

    val stackSaved: Int

    val memoryBytes: Int
    val memorySize: Int
    val unsafe: Int
    val byteArrayBase: Int

    val size: Int

    val frameTypes: Array<Any>

    val heldSlots: IntArray = slots

    val isRecording: Boolean get() = recording != null

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
        for (slot in slots) {
            val kind = slotKinds.kind(slot)
            slotLocals[slot] = local(kindFrameType(kind), if (kind == SlotKinds.LONG || kind == SlotKinds.DOUBLE) 2 else 1)
            if (slotKinds.floatShadow(slot)) floatShadows[slot] = local(Opcodes.FLOAT, 1)
            if (slotKinds.doubleShadow(slot)) doubleShadows[slot] = local(Opcodes.DOUBLE, 2)
        }
        for (index in 0 until SCRATCH_LONGS) scratchLong[index] = local(Opcodes.LONG, 2)
        scratchInt = local(Opcodes.INTEGER, 1)
        scratchValue = local(Opcodes.INTEGER, 1)
        scratchValues = local("[J", 1)
        scratchObject = local("java/lang/Object", 1)
        rootsSaved = local(Opcodes.LONG, 2)
        rootBase = local(Opcodes.INTEGER, 1)
        exitPosition = local(Opcodes.INTEGER, 1)
        stackSaved = local(Opcodes.LONG, 2)
        memoryBytes = if (inlineMemory != null) local("[B", 1) else -1
        memorySize = if (inlineMemory != null) local(Opcodes.INTEGER, 1) else -1
        unsafe = if (inlineMemory == InlineMemory.UNSAFE) local("sun/misc/Unsafe", 1) else -1
        byteArrayBase = if (inlineMemory == InlineMemory.UNSAFE) local(Opcodes.LONG, 2) else -1
        size = next
        frameTypes = types.toTypedArray()
    }

    val parameterLocals: IntArray get() = argumentLocals

    fun slot(
        slot: Int,
    ): Int {
        recording?.set(slot, true)
        val local = slotLocals[slot]
        check(local >= 0) { "slot $slot has no local here" }
        return local
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

    fun slotOf(
        local: Int,
    ): Int = localSlots.getOrElse(local) { -1 }

    private val localSlots: IntArray by lazy {
        IntArray(size) { -1 }.also { owners ->
            for (slot in slotLocals.indices) {
                listOf(slotLocals[slot], floatShadows[slot], doubleShadows[slot]).forEach { local ->
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
