package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.dex.analysis.SlotKindFlow

internal class SlotKindConflict(
    val slot: Int,
    val kind: Byte,
    val position: Int,
) : Exception("slot $slot is read with conflicting kinds")

internal open class HomeMismatch(
    message: String,
) : Exception(message) {
    constructor(
        slot: Int,
        position: Int,
    ) : this("unexpected write of slot $slot at $position")
}

internal class SlotKindCursor(
    private val homes: RegisterHomes?,
    frameSlots: Int,
) {
    private val state = ByteArray(frameSlots).also { state -> homes?.flow?.initialKinds(state) }

    private var tracked = Int.MIN_VALUE

    var position = PROLOGUE
        private set

    fun moveTo(position: Int): Boolean {

        this.position = position
        val flow = homes?.flow ?: return true
        if (position == PROLOGUE) {
            return true
        }

        if (tracked != position || flow.leader(position)) {
            flow.before(position, state)
        }
        tracked = position

        return flow.reached(position)
    }

    fun commit() {

        val flow = homes?.flow ?: return
        if (tracked != position) {
            return
        }

        flow.advance(position, state)
        tracked = position + 1
    }

    fun atLeader(): Boolean {
        return homes?.flow?.leader(position) == true
    }

    fun state(slot: Int): Byte {
        return if (homes == null) SlotKindFlow.LONG else state[slot]
    }

    fun commitWrite(
        slot: Int,
        kind: Byte,
    ) {
        val homes = homes ?: return
        if (homes.flow.tracks(slot)) {
            state[slot] = kind
        }
    }

    fun readKind(
        slot: Int,
        natural: Byte,
    ): Byte {
        val homes = homes ?: return SlotKindFlow.LONG
        return read(homes, slot, state[slot], natural)
    }

    fun readKindAfterWrites(
        slot: Int,
        natural: Byte,
    ): Byte {
        val homes = homes ?: return SlotKindFlow.LONG
        return read(homes, slot, homes.flow.advanced(position, slot, state), natural)
    }

    private fun read(
        homes: RegisterHomes,
        slot: Int,
        kind: Byte,
        natural: Byte,
    ): Byte {

        val pin = homes.pin(slot)
        if (pin != SlotKindFlow.UNDEFINED) {
            return pin
        }
        if (SlotKindFlow.definite(kind)) {
            return kind
        }
        if (kind >= SlotKindFlow.CONFLICT_NARROW) {
            throw SlotKindConflict(slot, kind, position)
        }

        val homeKind = anyHome(homes.mask(slot), natural)
        if (homeKind == SlotKindFlow.NONE) {
            throw HomeMismatch(slot, position)
        }
        return homeKind
    }

    fun writeKind(slot: Int): Byte {

        val homes = homes ?: return SlotKindFlow.LONG
        val pin = homes.pin(slot)
        if (position == PROLOGUE) {
            return if (pin != SlotKindFlow.UNDEFINED) pin else homes.flow.entryKind(slot)
        }

        val kind = homes.flow.written(position, slot)
        if (kind == SlotKindFlow.NONE) {
            throw HomeMismatch(slot, position)
        }
        if (kind == SlotKindFlow.UNDEFINED || pin == SlotKindFlow.UNDEFINED) {
            return unpinned(slot, kind)
        }
        return pin
    }

    fun handlerKind(slot: Int): Byte {
        val homes = homes ?: return SlotKindFlow.LONG
        val pin = homes.pin(slot)
        return if (pin != SlotKindFlow.UNDEFINED) pin else SlotKindFlow.LONG
    }

    fun representation(
        slot: Int,
        kind: Byte,
    ): Byte {
        val homes = homes ?: return SlotKindFlow.LONG
        val pin = homes.pin(slot)
        return if (kind == SlotKindFlow.UNDEFINED || pin == SlotKindFlow.UNDEFINED) unpinned(slot, kind) else pin
    }

    private fun unpinned(
        slot: Int,
        kind: Byte,
    ): Byte {
        if (kind >= SlotKindFlow.CONFLICT_NARROW) {
            throw SlotKindConflict(slot, kind, position)
        }
        return kind
    }

    fun represents(
        slot: Int,
        kind: Byte,
    ): Boolean {
        val homes = homes ?: return true
        return homes.pin(slot) != SlotKindFlow.UNDEFINED || state[slot] == kind
    }

    fun homeMask(slot: Int): Int {
        val homes = homes ?: return SlotKindFlow.homeBit(SlotKindFlow.LONG)
        return homes.mask(slot)
    }

    companion object {
        const val PROLOGUE = -1
    }
}
