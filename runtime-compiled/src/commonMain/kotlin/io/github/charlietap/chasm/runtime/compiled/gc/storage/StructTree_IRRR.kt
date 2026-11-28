package io.github.charlietap.chasm.runtime.compiled.gc.storage

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.compiled.gc.CompiledStruct
import io.github.charlietap.chasm.runtime.compiled.gc.Rtt
import io.github.charlietap.chasm.runtime.compiled.gc.storeLane
import kotlin.jvm.JvmField

@InternalChasmApi
open class Struct_I1_R3__IRRR(rtt: Rtt) : CompiledStruct(rtt) {

    @JvmField
    var i0: Int = 0

    @JvmField
    var r0: Any? = null

    @JvmField
    var r1: Any? = null

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
            0 -> r0
            1 -> r1
            2 -> r2
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
            0 -> r0 = value
            1 -> r1 = value
            2 -> r2 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I2_R3__IRRR_I(rtt: Rtt) : Struct_I1_R3__IRRR(rtt) {

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
        storeLane(destinations[1], i1, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            1 -> i1
            else -> super.readInt(lane)
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
}

@InternalChasmApi
open class Struct_I4_R3__IRRR_I_II(rtt: Rtt) : Struct_I2_R3__IRRR_I(rtt) {

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
open class Struct_I3_R4__IRRR_I_IR(rtt: Rtt) : Struct_I2_R3__IRRR_I(rtt) {

    @JvmField
    var i2: Int = 0

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
open class Struct_I2_R5__IRRR_I_RR(rtt: Rtt) : Struct_I2_R3__IRRR_I(rtt) {

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
open class Struct_I1_R4__IRRR_R(rtt: Rtt) : Struct_I1_R3__IRRR(rtt) {

    @JvmField
    var r3: Any? = null

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            3 -> r3
            else -> super.readReference(lane)
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
open class Struct_I3_R4__IRRR_R_II(rtt: Rtt) : Struct_I1_R4__IRRR_R(rtt) {

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
open class Struct_I2_R5__IRRR_R_IR(rtt: Rtt) : Struct_I1_R4__IRRR_R(rtt) {

    @JvmField
    var i1: Int = 0

    @JvmField
    var r4: Any? = null

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
open class Struct_I1_R6__IRRR_R_RR(rtt: Rtt) : Struct_I1_R4__IRRR_R(rtt) {

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

@InternalChasmApi
open class Struct_I3_R3__IRRR_II(rtt: Rtt) : Struct_I1_R3__IRRR(rtt) {

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
open class Struct_I5_R3__IRRR_II_II(rtt: Rtt) : Struct_I3_R3__IRRR_II(rtt) {

    @JvmField
    var i3: Int = 0

    @JvmField
    var i4: Int = 0

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[3], i3, buffer, shift, from, until)
        storeLane(destinations[4], i4, buffer, shift, from, until)
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
open class Struct_I4_R4__IRRR_II_IR(rtt: Rtt) : Struct_I3_R3__IRRR_II(rtt) {

    @JvmField
    var i3: Int = 0

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
        storeLane(destinations[3], i3, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            3 -> i3
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
            3 -> i3 = value
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
open class Struct_I3_R5__IRRR_II_RR(rtt: Rtt) : Struct_I3_R3__IRRR_II(rtt) {

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
open class Struct_I2_R4__IRRR_IR(rtt: Rtt) : Struct_I1_R3__IRRR(rtt) {

    @JvmField
    var i1: Int = 0

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
            3 -> r3
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
            3 -> r3 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I4_R4__IRRR_IR_II(rtt: Rtt) : Struct_I2_R4__IRRR_IR(rtt) {

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
open class Struct_I3_R5__IRRR_IR_IR(rtt: Rtt) : Struct_I2_R4__IRRR_IR(rtt) {

    @JvmField
    var i2: Int = 0

    @JvmField
    var r4: Any? = null

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
            4 -> r4
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
            4 -> r4 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I2_R6__IRRR_IR_RR(rtt: Rtt) : Struct_I2_R4__IRRR_IR(rtt) {

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

@InternalChasmApi
open class Struct_I1_R5__IRRR_RR(rtt: Rtt) : Struct_I1_R3__IRRR(rtt) {

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
open class Struct_I3_R5__IRRR_RR_II(rtt: Rtt) : Struct_I1_R5__IRRR_RR(rtt) {

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
open class Struct_I2_R6__IRRR_RR_IR(rtt: Rtt) : Struct_I1_R5__IRRR_RR(rtt) {

    @JvmField
    var i1: Int = 0

    @JvmField
    var r5: Any? = null

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
            5 -> r5
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
            5 -> r5 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I1_R7__IRRR_RR_RR(rtt: Rtt) : Struct_I1_R5__IRRR_RR(rtt) {

    @JvmField
    var r5: Any? = null

    @JvmField
    var r6: Any? = null

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            5 -> r5
            6 -> r6
            else -> super.readReference(lane)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            5 -> r5 = value
            6 -> r6 = value
            else -> super.writeReference(lane, value)
        }
    }
}
