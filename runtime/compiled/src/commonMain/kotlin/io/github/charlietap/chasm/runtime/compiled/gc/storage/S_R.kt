package io.github.charlietap.chasm.runtime.compiled.gc.storage

import io.github.charlietap.chasm.runtime.compiled.gc.CompiledStruct
import io.github.charlietap.chasm.runtime.compiled.gc.Rtt
import kotlin.jvm.JvmField

open class S_R(rtt: Rtt) : CompiledStruct(rtt) {

    @JvmField
    var r0: Any? = null

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            0 -> r0
            else -> super.readReference(lane)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            0 -> r0 = value
            else -> super.writeReference(lane, value)
        }
    }
}
