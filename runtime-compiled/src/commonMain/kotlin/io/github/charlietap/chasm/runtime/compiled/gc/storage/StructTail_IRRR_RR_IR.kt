package io.github.charlietap.chasm.runtime.compiled.gc.storage

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.compiled.gc.Rtt
import io.github.charlietap.chasm.runtime.compiled.gc.storeLane
import kotlin.jvm.JvmField

@InternalChasmApi
open class Struct_I3_R7__IRRR_RR_IR_IRx1(rtt: Rtt) : Struct_I2_R6__IRRR_RR_IR(rtt) {

    @JvmField
    var i2: Int = 0

    @JvmField
    var r6: Any? = null

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
            6 -> r6
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
            6 -> r6 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I4_R8__IRRR_RR_IR_IRx2(rtt: Rtt) : Struct_I3_R7__IRRR_RR_IR_IRx1(rtt) {

    @JvmField
    var i3: Int = 0

    @JvmField
    var r7: Any? = null

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
            7 -> r7
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
            7 -> r7 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I5_R9__IRRR_RR_IR_IRx3(rtt: Rtt) : Struct_I4_R8__IRRR_RR_IR_IRx2(rtt) {

    @JvmField
    var i4: Int = 0

    @JvmField
    var r8: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[4], i4, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            4 -> i4
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            8 -> r8
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
            8 -> r8 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I6_R10__IRRR_RR_IR_IRx4(rtt: Rtt) : Struct_I5_R9__IRRR_RR_IR_IRx3(rtt) {

    @JvmField
    var i5: Int = 0

    @JvmField
    var r9: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[5], i5, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            5 -> i5
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            9 -> r9
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
            9 -> r9 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I7_R11__IRRR_RR_IR_IRx5(rtt: Rtt) : Struct_I6_R10__IRRR_RR_IR_IRx4(rtt) {

    @JvmField
    var i6: Int = 0

    @JvmField
    var r10: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[6], i6, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            6 -> i6
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            10 -> r10
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            6 -> i6 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            10 -> r10 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I8_R12__IRRR_RR_IR_IRx6(rtt: Rtt) : Struct_I7_R11__IRRR_RR_IR_IRx5(rtt) {

    @JvmField
    var i7: Int = 0

    @JvmField
    var r11: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[7], i7, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            7 -> i7
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            11 -> r11
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            7 -> i7 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            11 -> r11 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I9_R13__IRRR_RR_IR_IRx7(rtt: Rtt) : Struct_I8_R12__IRRR_RR_IR_IRx6(rtt) {

    @JvmField
    var i8: Int = 0

    @JvmField
    var r12: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[8], i8, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            8 -> i8
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            12 -> r12
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            8 -> i8 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            12 -> r12 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I10_R14__IRRR_RR_IR_IRx8(rtt: Rtt) : Struct_I9_R13__IRRR_RR_IR_IRx7(rtt) {

    @JvmField
    var i9: Int = 0

    @JvmField
    var r13: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[9], i9, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            9 -> i9
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            13 -> r13
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            9 -> i9 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            13 -> r13 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I11_R15__IRRR_RR_IR_IRx9(rtt: Rtt) : Struct_I10_R14__IRRR_RR_IR_IRx8(rtt) {

    @JvmField
    var i10: Int = 0

    @JvmField
    var r14: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[10], i10, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            10 -> i10
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            14 -> r14
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            10 -> i10 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            14 -> r14 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I12_R16__IRRR_RR_IR_IRx10(rtt: Rtt) : Struct_I11_R15__IRRR_RR_IR_IRx9(rtt) {

    @JvmField
    var i11: Int = 0

    @JvmField
    var r15: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[11], i11, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            11 -> i11
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            15 -> r15
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            11 -> i11 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            15 -> r15 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I13_R17__IRRR_RR_IR_IRx11(rtt: Rtt) : Struct_I12_R16__IRRR_RR_IR_IRx10(rtt) {

    @JvmField
    var i12: Int = 0

    @JvmField
    var r16: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[12], i12, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            12 -> i12
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            16 -> r16
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            12 -> i12 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            16 -> r16 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I14_R18__IRRR_RR_IR_IRx12(rtt: Rtt) : Struct_I13_R17__IRRR_RR_IR_IRx11(rtt) {

    @JvmField
    var i13: Int = 0

    @JvmField
    var r17: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[13], i13, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            13 -> i13
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            17 -> r17
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            13 -> i13 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            17 -> r17 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I15_R19__IRRR_RR_IR_IRx13(rtt: Rtt) : Struct_I14_R18__IRRR_RR_IR_IRx12(rtt) {

    @JvmField
    var i14: Int = 0

    @JvmField
    var r18: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[14], i14, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            14 -> i14
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            18 -> r18
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            14 -> i14 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            18 -> r18 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I16_R20__IRRR_RR_IR_IRx14(rtt: Rtt) : Struct_I15_R19__IRRR_RR_IR_IRx13(rtt) {

    @JvmField
    var i15: Int = 0

    @JvmField
    var r19: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[15], i15, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            15 -> i15
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            19 -> r19
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            15 -> i15 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            19 -> r19 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I17_R21__IRRR_RR_IR_IRx15(rtt: Rtt) : Struct_I16_R20__IRRR_RR_IR_IRx14(rtt) {

    @JvmField
    var i16: Int = 0

    @JvmField
    var r20: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[16], i16, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            16 -> i16
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            20 -> r20
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            16 -> i16 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            20 -> r20 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
open class Struct_I18_R22__IRRR_RR_IR_IRx16(rtt: Rtt) : Struct_I17_R21__IRRR_RR_IR_IRx15(rtt) {

    @JvmField
    var i17: Int = 0

    @JvmField
    var r21: Any? = null

    override fun readNumberLanes(
        destinations: IntArray,
        buffer: LongArray,
        shift: Int,
        from: Int,
        until: Int,
    ) {
        super.readNumberLanes(destinations, buffer, shift, from, until)
        storeLane(destinations[17], i17, buffer, shift, from, until)
    }

    override fun readInt(lane: Int): Int {
        return when (lane) {
            17 -> i17
            else -> super.readInt(lane)
        }
    }

    override fun readReference(lane: Int): Any? {
        return when (lane) {
            21 -> r21
            else -> super.readReference(lane)
        }
    }

    override fun writeInt(
        lane: Int,
        value: Int,
    ) {
        when (lane) {
            17 -> i17 = value
            else -> super.writeInt(lane, value)
        }
    }

    override fun writeReference(
        lane: Int,
        value: Any?,
    ) {
        when (lane) {
            21 -> r21 = value
            else -> super.writeReference(lane, value)
        }
    }
}

@InternalChasmApi
class Struct_I18_R22__IRRR_RR_IR_IRx16_Spill(rtt: Rtt) : Struct_I18_R22__IRRR_RR_IR_IRx16(rtt) {

    @JvmField
    var spilledInts: IntArray? = null

    @JvmField
    var spilledReferences: Array<Any?>? = null

    override fun readSpilledInt(slot: Int): Int {
        val ints = spilledInts
        if (ints != null && slot in ints.indices) {
            return ints[slot]
        } else {
            return super.readSpilledInt(slot)
        }
    }

    override fun readSpilledReference(slot: Int): Any? {
        val references = spilledReferences
        if (references != null && slot in references.indices) {
            return references[slot]
        } else {
            return super.readSpilledReference(slot)
        }
    }

    override fun writeSpilledInt(
        slot: Int,
        value: Int,
    ) {
        val ints = spilledInts
        if (ints != null && slot in ints.indices) {
            ints[slot] = value
        } else {
            super.writeSpilledInt(slot, value)
        }
    }

    override fun writeSpilledReference(
        slot: Int,
        value: Any?,
    ) {
        val references = spilledReferences
        if (references != null && slot in references.indices) {
            references[slot] = value
        } else {
            super.writeSpilledReference(slot, value)
        }
    }
}
