package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.chasm.gc.GcRootMarker
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.runtime.heap.HeldRoots

internal interface HeldReference {

    val slot: Int
}

internal class HeldStruct(
    private val binding: EngineBinding,
    override val slot: Int,
    val raw: Long,
    val runtimeType: Int,
) : StructReference(),
    HeldReference {

    override fun engine(): Engine {
        return binding.engine
    }

    override fun equals(other: Any?): Boolean {
        return other is HeldStruct && other.binding === binding && other.raw == raw
    }

    override fun hashCode(): Int {
        return raw.hashCode()
    }
}

internal class HeldArray(
    private val binding: EngineBinding,
    override val slot: Int,
    val raw: Long,
    val runtimeType: Int,
) : ArrayReference(),
    HeldReference {

    override fun engine(): Engine {
        return binding.engine
    }

    override fun equals(other: Any?): Boolean {
        return other is HeldArray && other.binding === binding && other.raw == raw
    }

    override fun hashCode(): Int {
        return raw.hashCode()
    }
}

internal class HeldException(
    private val binding: EngineBinding,
    override val slot: Int,
    val raw: Long,
) : ExceptionReference(),
    HeldReference {

    override fun engine(): Engine {
        return binding.engine
    }

    override fun equals(other: Any?): Boolean {
        return other is HeldException && other.binding === binding && other.raw == raw
    }

    override fun hashCode(): Int {
        return raw.hashCode()
    }
}

internal interface HeldReferences : HeldRoots {

    fun struct(
        raw: Long,
        runtimeType: Int,
    ): HeldStruct

    fun array(
        raw: Long,
        runtimeType: Int,
    ): HeldArray

    fun exception(raw: Long): HeldException

    fun clear()
}

internal class HeldReferenceTable(
    private val binding: EngineBinding,
) : HeldReferences {

    private var raws = LongArray(INITIAL_CAPACITY)
    private var holders = arrayOfNulls<PlatformWeakReference<HeldReference>>(INITIAL_CAPACITY)
    private var nextFree = IntArray(INITIAL_CAPACITY)
    private var freeHead = NO_SLOT
    private var top = 0
    private var live = 0
    private var highWater = INITIAL_HIGH_WATER

    override fun struct(
        raw: Long,
        runtimeType: Int,
    ): HeldStruct {

        val slot = claim(raw)
        val held = HeldStruct(binding, slot, raw, runtimeType)
        holders[slot] = PlatformWeakReference(held)

        return held
    }

    override fun array(
        raw: Long,
        runtimeType: Int,
    ): HeldArray {

        val slot = claim(raw)
        val held = HeldArray(binding, slot, raw, runtimeType)
        holders[slot] = PlatformWeakReference(held)

        return held
    }

    override fun exception(raw: Long): HeldException {

        val slot = claim(raw)
        val held = HeldException(binding, slot, raw)
        holders[slot] = PlatformWeakReference(held)

        return held
    }

    override fun visit(marker: GcRootMarker) {
        for (slot in 0 until top) {

            val holder = holders[slot] ?: continue

            if (holder.get() == null) {
                release(slot)
            } else {
                marker.markRoot(raws[slot])
            }
        }
    }

    override fun clear() {
        raws = LongArray(INITIAL_CAPACITY)
        holders = arrayOfNulls(INITIAL_CAPACITY)
        nextFree = IntArray(INITIAL_CAPACITY)
        freeHead = NO_SLOT
        top = 0
        live = 0
        highWater = INITIAL_HIGH_WATER
    }

    private fun claim(raw: Long): Int {

        if (live >= highWater) {
            trim()
            highWater = maxOf(INITIAL_HIGH_WATER, live * 2)
        }

        val slot = nextFreeSlot()
        raws[slot] = raw
        live++

        return slot
    }

    private fun trim() {
        for (slot in 0 until top) {

            val holder = holders[slot] ?: continue

            if (holder.get() == null) {
                release(slot)
            }
        }
    }

    private fun release(slot: Int) {
        holders[slot] = null
        raws[slot] = 0L
        nextFree[slot] = freeHead
        freeHead = slot
        live--
    }

    private fun nextFreeSlot(): Int {

        if (freeHead != NO_SLOT) {
            val slot = freeHead
            freeHead = nextFree[slot]
            return slot
        }

        if (top == raws.size) {
            val capacity = raws.size * 2
            raws = raws.copyOf(capacity)
            holders = holders.copyOf(capacity)
            nextFree = nextFree.copyOf(capacity)
        }

        return top++
    }

    private companion object {
        const val INITIAL_CAPACITY = 64
        const val INITIAL_HIGH_WATER = 64
        const val NO_SLOT = -1
    }
}
