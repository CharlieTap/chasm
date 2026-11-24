package io.github.charlietap.chasm.runtime.compiled.gc.storage

import io.github.charlietap.chasm.runtime.compiled.gc.Rtt
import kotlin.jvm.JvmField

open class S_R_IR_IR_RR_T1(rtt: Rtt) : S_R_IR_IR_RR(rtt) {

    @JvmField
    var i2: Int = 0

    @JvmField
    var r5: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            2 -> i2
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
            2 -> i2 = value
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

open class S_R_IR_IR_RR_T2(rtt: Rtt) : S_R_IR_IR_RR_T1(rtt) {

    @JvmField
    var i3: Int = 0

    @JvmField
    var r6: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            3 -> i3
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
            3 -> i3 = value
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

open class S_R_IR_IR_RR_T3(rtt: Rtt) : S_R_IR_IR_RR_T2(rtt) {

    @JvmField
    var i4: Int = 0

    @JvmField
    var r7: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            4 -> i4
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
            4 -> i4 = value
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

open class S_R_IR_IR_RR_T4(rtt: Rtt) : S_R_IR_IR_RR_T3(rtt) {

    @JvmField
    var i5: Int = 0

    @JvmField
    var r8: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            5 -> i5
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
            5 -> i5 = value
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

open class S_R_IR_IR_RR_T5(rtt: Rtt) : S_R_IR_IR_RR_T4(rtt) {

    @JvmField
    var i6: Int = 0

    @JvmField
    var r9: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            6 -> i6
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
            6 -> i6 = value
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

open class S_R_IR_IR_RR_T6(rtt: Rtt) : S_R_IR_IR_RR_T5(rtt) {

    @JvmField
    var i7: Int = 0

    @JvmField
    var r10: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            7 -> i7
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
            7 -> i7 = value
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

open class S_R_IR_IR_RR_T7(rtt: Rtt) : S_R_IR_IR_RR_T6(rtt) {

    @JvmField
    var i8: Int = 0

    @JvmField
    var r11: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            8 -> i8
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
            8 -> i8 = value
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

open class S_R_IR_IR_RR_T8(rtt: Rtt) : S_R_IR_IR_RR_T7(rtt) {

    @JvmField
    var i9: Int = 0

    @JvmField
    var r12: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            9 -> i9
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
            9 -> i9 = value
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

open class S_R_IR_IR_RR_T9(rtt: Rtt) : S_R_IR_IR_RR_T8(rtt) {

    @JvmField
    var i10: Int = 0

    @JvmField
    var r13: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            10 -> i10
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
            10 -> i10 = value
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

open class S_R_IR_IR_RR_T10(rtt: Rtt) : S_R_IR_IR_RR_T9(rtt) {

    @JvmField
    var i11: Int = 0

    @JvmField
    var r14: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            11 -> i11
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
            11 -> i11 = value
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

open class S_R_IR_IR_RR_T11(rtt: Rtt) : S_R_IR_IR_RR_T10(rtt) {

    @JvmField
    var i12: Int = 0

    @JvmField
    var r15: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            12 -> i12
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
            12 -> i12 = value
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

open class S_R_IR_IR_RR_T12(rtt: Rtt) : S_R_IR_IR_RR_T11(rtt) {

    @JvmField
    var i13: Int = 0

    @JvmField
    var r16: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            13 -> i13
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
            13 -> i13 = value
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

open class S_R_IR_IR_RR_T13(rtt: Rtt) : S_R_IR_IR_RR_T12(rtt) {

    @JvmField
    var i14: Int = 0

    @JvmField
    var r17: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            14 -> i14
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
            14 -> i14 = value
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

open class S_R_IR_IR_RR_T14(rtt: Rtt) : S_R_IR_IR_RR_T13(rtt) {

    @JvmField
    var i15: Int = 0

    @JvmField
    var r18: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            15 -> i15
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
            15 -> i15 = value
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

open class S_R_IR_IR_RR_T15(rtt: Rtt) : S_R_IR_IR_RR_T14(rtt) {

    @JvmField
    var i16: Int = 0

    @JvmField
    var r19: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            16 -> i16
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
            16 -> i16 = value
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

open class S_R_IR_IR_RR_T16(rtt: Rtt) : S_R_IR_IR_RR_T15(rtt) {

    @JvmField
    var i17: Int = 0

    @JvmField
    var r20: Any? = null

    override fun readInt(lane: Int): Int {
        return when (lane) {
            17 -> i17
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
            17 -> i17 = value
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

class S_R_IR_IR_RR_X(rtt: Rtt) : S_R_IR_IR_RR_T16(rtt) {

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
