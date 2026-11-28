package io.github.charlietap.chasm.runtime.compiled.gc.storage

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.compiled.gc.CompiledStruct
import io.github.charlietap.chasm.runtime.compiled.gc.Rtt
import kotlin.jvm.JvmField

@InternalChasmApi
open class S_I(rtt: Rtt) : CompiledStruct(rtt) {

    @JvmField
    var i0: Int = 0

    override fun readInts(target: IntArray) {
        super.readInts(target)
        target[0] = i0
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            0 -> i0
            else -> super.readInt(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            0 -> i0 = value
            else -> super.writeInt(lane, value)
        }
    }
}
