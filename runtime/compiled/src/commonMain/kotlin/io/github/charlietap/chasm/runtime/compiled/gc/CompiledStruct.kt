package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.embedding.engine.Engine
import kotlin.jvm.JvmField

abstract class CompiledStruct(
    @JvmField val rtt: Rtt,
) : ManagedStruct() {

    final override fun engine(): Engine {
        return rtt.engine
    }

    open fun readInt(lane: Int): Int {
        throw laneError(lane)
    }

    open fun readReference(lane: Int): Any? {
        throw laneError(lane)
    }

    open fun writeInt(
        lane: Int,
        value: Int,
    ) {
        throw laneError(lane)
    }

    open fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        throw laneError(lane)
    }

    open fun readSpilledInt(slot: Int): Int {
        throw laneError(slot)
    }

    open fun readSpilledReference(slot: Int): Any? {
        throw laneError(slot)
    }

    open fun writeSpilledInt(
        slot: Int,
        value: Int,
    ) {
        throw laneError(slot)
    }

    open fun writeSpilledReference(
        slot: Int,
        value: Any?,
    ) {
        throw laneError(slot)
    }
}
