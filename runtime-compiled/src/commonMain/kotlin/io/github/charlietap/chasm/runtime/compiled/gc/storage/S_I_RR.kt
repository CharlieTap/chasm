package io.github.charlietap.chasm.runtime.compiled.gc.storage

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.compiled.gc.Rtt
import kotlin.jvm.JvmField

@InternalChasmApi
open class S_I_RR(rtt: Rtt) : S_I(rtt) {

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

@InternalChasmApi
open class S_I_RR_II(rtt: Rtt) : S_I_RR(rtt) {

    @JvmField
    var i1: Int = 0

    @JvmField
    var i2: Int = 0

    override fun readInts(target: IntArray) {
        super.readInts(target)
        target[1] = i1
        target[2] = i2
    }

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

@InternalChasmApi
open class S_I_RR_II_II(rtt: Rtt) : S_I_RR_II(rtt) {

    @JvmField
    var i3: Int = 0

    @JvmField
    var i4: Int = 0

    override fun readInts(target: IntArray) {
        super.readInts(target)
        target[3] = i3
        target[4] = i4
    }

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

@InternalChasmApi
open class S_I_RR_II_IR(rtt: Rtt) : S_I_RR_II(rtt) {

    @JvmField
    var i3: Int = 0

    @JvmField
    var r2: Any? = null

    override fun readInts(target: IntArray) {
        super.readInts(target)
        target[3] = i3
    }

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

@InternalChasmApi
open class S_I_RR_II_RR(rtt: Rtt) : S_I_RR_II(rtt) {

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

@InternalChasmApi
open class S_I_RR_IR(rtt: Rtt) : S_I_RR(rtt) {

    @JvmField
    var i1: Int = 0

    @JvmField
    var r2: Any? = null

    override fun readInts(target: IntArray) {
        super.readInts(target)
        target[1] = i1
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            1 -> i1
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
            1 -> i1 = value
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

@InternalChasmApi
open class S_I_RR_IR_II(rtt: Rtt) : S_I_RR_IR(rtt) {

    @JvmField
    var i2: Int = 0

    @JvmField
    var i3: Int = 0

    override fun readInts(target: IntArray) {
        super.readInts(target)
        target[2] = i2
        target[3] = i3
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            2 -> i2
            3 -> i3
            else -> super.readInt(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            2 -> i2 = value
            3 -> i3 = value
            else -> super.writeInt(lane, value)
        }
    }
}

@InternalChasmApi
open class S_I_RR_IR_IR(rtt: Rtt) : S_I_RR_IR(rtt) {

    @JvmField
    var i2: Int = 0

    @JvmField
    var r3: Any? = null

    override fun readInts(target: IntArray) {
        super.readInts(target)
        target[2] = i2
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            2 -> i2
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            3 -> r3
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            2 -> i2 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            3 -> r3 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class S_I_RR_IR_RR(rtt: Rtt) : S_I_RR_IR(rtt) {

    @JvmField
    var r3: Any? = null

    @JvmField
    var r4: Any? = null

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            3 -> r3
            4 -> r4
            else -> super.readReference(lane)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            3 -> r3 = value
            4 -> r4 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class S_I_RR_RR(rtt: Rtt) : S_I_RR(rtt) {

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

@InternalChasmApi
open class S_I_RR_RR_II(rtt: Rtt) : S_I_RR_RR(rtt) {

    @JvmField
    var i1: Int = 0

    @JvmField
    var i2: Int = 0

    override fun readInts(target: IntArray) {
        super.readInts(target)
        target[1] = i1
        target[2] = i2
    }

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

@InternalChasmApi
open class S_I_RR_RR_IR(rtt: Rtt) : S_I_RR_RR(rtt) {

    @JvmField
    var i1: Int = 0

    @JvmField
    var r4: Any? = null

    override fun readInts(target: IntArray) {
        super.readInts(target)
        target[1] = i1
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            1 -> i1
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            4 -> r4
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            1 -> i1 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            4 -> r4 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class S_I_RR_RR_RR(rtt: Rtt) : S_I_RR_RR(rtt) {

    @JvmField
    var r4: Any? = null

    @JvmField
    var r5: Any? = null

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            4 -> r4
            5 -> r5
            else -> super.readReference(lane)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            4 -> r4 = value
            5 -> r5 = value
            else -> super.writeReference(lane, value)
        }
    }
}
