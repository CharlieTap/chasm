package io.github.charlietap.chasm.runtime.compiled.gc.storage

import io.github.charlietap.chasm.runtime.compiled.gc.Rtt
import kotlin.jvm.JvmField

open class S_I_II(rtt: Rtt) : S_I(rtt) {

    @JvmField
    var i1: Int = 0

    @JvmField
    var i2: Int = 0

    override fun readInt(lane: Int): Int {
        return when (lane) {
            1 -> i1
            2 -> i2
            else -> super.readInt(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            1 -> i1 = value
            2 -> i2 = value
            else -> super.writeInt(lane, value)
        }
    }
}

open class S_I_II_II(rtt: Rtt) : S_I_II(rtt) {

    @JvmField
    var i3: Int = 0

    @JvmField
    var i4: Int = 0

    override fun readInt(lane: Int): Int {
        return when (lane) {
            3 -> i3
            4 -> i4
            else -> super.readInt(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            3 -> i3 = value
            4 -> i4 = value
            else -> super.writeInt(lane, value)
        }
    }
}

open class S_I_II_II_II(rtt: Rtt) : S_I_II_II(rtt) {

    @JvmField
    var i5: Int = 0

    @JvmField
    var i6: Int = 0

    override fun readInt(lane: Int): Int {
        return when (lane) {
            5 -> i5
            6 -> i6
            else -> super.readInt(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            5 -> i5 = value
            6 -> i6 = value
            else -> super.writeInt(lane, value)
        }
    }
}

open class S_I_II_II_IR(rtt: Rtt) : S_I_II_II(rtt) {

    @JvmField
    var i5: Int = 0

    @JvmField
    var r0: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            5 -> i5
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            0 -> r0
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            5 -> i5 = value
            else -> super.writeInt(lane, value)
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

open class S_I_II_II_RR(rtt: Rtt) : S_I_II_II(rtt) {

    @JvmField
    var r0: Any? = null

    @JvmField
    var r1: Any? = null

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            0 -> r0
            1 -> r1
            else -> super.readReference(lane)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            0 -> r0 = value
            1 -> r1 = value
            else -> super.writeReference(lane, value)
        }
    }
}

open class S_I_II_IR(rtt: Rtt) : S_I_II(rtt) {

    @JvmField
    var i3: Int = 0

    @JvmField
    var r0: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            3 -> i3
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            0 -> r0
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            3 -> i3 = value
            else -> super.writeInt(lane, value)
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

open class S_I_II_IR_II(rtt: Rtt) : S_I_II_IR(rtt) {

    @JvmField
    var i4: Int = 0

    @JvmField
    var i5: Int = 0

    override fun readInt(lane: Int): Int {
        return when (lane) {
            4 -> i4
            5 -> i5
            else -> super.readInt(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            4 -> i4 = value
            5 -> i5 = value
            else -> super.writeInt(lane, value)
        }
    }
}

open class S_I_II_IR_IR(rtt: Rtt) : S_I_II_IR(rtt) {

    @JvmField
    var i4: Int = 0

    @JvmField
    var r1: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            4 -> i4
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            1 -> r1
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            4 -> i4 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            1 -> r1 = value
            else -> super.writeReference(lane, value)
        }
    }
}

open class S_I_II_IR_RR(rtt: Rtt) : S_I_II_IR(rtt) {

    @JvmField
    var r1: Any? = null

    @JvmField
    var r2: Any? = null

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            1 -> r1
            2 -> r2
            else -> super.readReference(lane)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            1 -> r1 = value
            2 -> r2 = value
            else -> super.writeReference(lane, value)
        }
    }
}

open class S_I_II_RR(rtt: Rtt) : S_I_II(rtt) {

    @JvmField
    var r0: Any? = null

    @JvmField
    var r1: Any? = null

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            0 -> r0
            1 -> r1
            else -> super.readReference(lane)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            0 -> r0 = value
            1 -> r1 = value
            else -> super.writeReference(lane, value)
        }
    }
}

open class S_I_II_RR_II(rtt: Rtt) : S_I_II_RR(rtt) {

    @JvmField
    var i3: Int = 0

    @JvmField
    var i4: Int = 0

    override fun readInt(lane: Int): Int {
        return when (lane) {
            3 -> i3
            4 -> i4
            else -> super.readInt(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            3 -> i3 = value
            4 -> i4 = value
            else -> super.writeInt(lane, value)
        }
    }
}

open class S_I_II_RR_IR(rtt: Rtt) : S_I_II_RR(rtt) {

    @JvmField
    var i3: Int = 0

    @JvmField
    var r2: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            3 -> i3
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            2 -> r2
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            3 -> i3 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            2 -> r2 = value
            else -> super.writeReference(lane, value)
        }
    }
}

open class S_I_II_RR_RR(rtt: Rtt) : S_I_II_RR(rtt) {

    @JvmField
    var r2: Any? = null

    @JvmField
    var r3: Any? = null

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            2 -> r2
            3 -> r3
            else -> super.readReference(lane)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            2 -> r2 = value
            3 -> r3 = value
            else -> super.writeReference(lane, value)
        }
    }
}
