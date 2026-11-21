package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.dex.analysis.SlotKindFlow

internal class RegisterHomes(
    val flow: SlotKindFlow,
    private val initialised: BooleanArray,
) {
    private var masks: ByteArray? = null

    private var pins: ByteArray? = null

    fun mask(slot: Int): Int {
        val masks = masks
        return if (masks == null) flow.homeMask(slot) else masks[slot].toInt()
    }

    fun pin(slot: Int): Byte {
        val pins = pins
        return if (pins == null) flow.pin(slot) else pins[slot]
    }

    fun pinConflict(slot: Int) {

        val pinned = pins ?: ByteArray(initialised.size) { index -> flow.pin(index) }
        val homes = masks ?: ByteArray(initialised.size) { index -> flow.homeMask(index).toByte() }

        val kind = SlotKindFlow.pinnedKind(homes[slot].toInt())
        pinned[slot] = kind
        homes[slot] = (SlotKindFlow.homeBit(kind) or (homes[slot].toInt() and SlotKindFlow.homeBit(SlotKindFlow.OBJECT))).toByte()

        pins = pinned
        masks = homes
    }

    fun reusableFor(initialised: BooleanArray): Boolean {
        return masks == null && this.initialised.contentEquals(initialised)
    }
}
