package io.github.charlietap.chasm.runtime.compiled.gc.storage

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.compiled.gc.CompiledStruct
import io.github.charlietap.chasm.runtime.compiled.gc.Rtt
import io.github.charlietap.chasm.runtime.compiled.gc.storeLane
import kotlin.jvm.JvmField

@InternalChasmApi
open class Struct_I0_R1__R(rtt: Rtt) : CompiledStruct(rtt) {

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

@InternalChasmApi
open class Struct_I2_R1__R_II(rtt: Rtt) : Struct_I0_R1__R(rtt) {

    @JvmField
    var i0: Int = 0

    @JvmField
    var i1: Int = 0

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[0], i0, buffer, shift, from, until)
        storeLane(destinations[1], i1, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            0 -> i0
            1 -> i1
            else -> super.readInt(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            0 -> i0 = value
            1 -> i1 = value
            else -> super.writeInt(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I4_R1__R_II_II(rtt: Rtt) : Struct_I2_R1__R_II(rtt) {

    @JvmField
    var i2: Int = 0

    @JvmField
    var i3: Int = 0

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[2], i2, buffer, shift, from, until)
        storeLane(destinations[3], i3, buffer, shift, from, until)
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
open class Struct_I3_R2__R_II_IR(rtt: Rtt) : Struct_I2_R1__R_II(rtt) {

    @JvmField
    var i2: Int = 0

    @JvmField
    var r1: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[2], i2, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            2 -> i2
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
            2 -> i2 = value
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

@InternalChasmApi
open class Struct_I2_R3__R_II_RR(rtt: Rtt) : Struct_I2_R1__R_II(rtt) {

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

@InternalChasmApi
open class Struct_I1_R2__R_IR(rtt: Rtt) : Struct_I0_R1__R(rtt) {

    @JvmField
    var i0: Int = 0

    @JvmField
    var r1: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[0], i0, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            0 -> i0
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
            0 -> i0 = value
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

@InternalChasmApi
open class Struct_I3_R2__R_IR_II(rtt: Rtt) : Struct_I1_R2__R_IR(rtt) {

    @JvmField
    var i1: Int = 0

    @JvmField
    var i2: Int = 0

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[1], i1, buffer, shift, from, until)
        storeLane(destinations[2], i2, buffer, shift, from, until)
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
open class Struct_I2_R3__R_IR_IR(rtt: Rtt) : Struct_I1_R2__R_IR(rtt) {

    @JvmField
    var i1: Int = 0

    @JvmField
    var r2: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[1], i1, buffer, shift, from, until)
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
open class Struct_I1_R4__R_IR_RR(rtt: Rtt) : Struct_I1_R2__R_IR(rtt) {

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
open class Struct_I0_R3__R_RR(rtt: Rtt) : Struct_I0_R1__R(rtt) {

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

@InternalChasmApi
open class Struct_I2_R3__R_RR_II(rtt: Rtt) : Struct_I0_R3__R_RR(rtt) {

    @JvmField
    var i0: Int = 0

    @JvmField
    var i1: Int = 0

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[0], i0, buffer, shift, from, until)
        storeLane(destinations[1], i1, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            0 -> i0
            1 -> i1
            else -> super.readInt(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            0 -> i0 = value
            1 -> i1 = value
            else -> super.writeInt(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I1_R4__R_RR_IR(rtt: Rtt) : Struct_I0_R3__R_RR(rtt) {

    @JvmField
    var i0: Int = 0

    @JvmField
    var r3: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[0], i0, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            0 -> i0
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
            0 -> i0 = value
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
open class Struct_I0_R5__R_RR_RR(rtt: Rtt) : Struct_I0_R3__R_RR(rtt) {

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
