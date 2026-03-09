package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun executeI64Add128(
    vstack: ValueStack,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
    leftLow: Long,
    leftHigh: Long,
    rightLow: Long,
    rightHigh: Long,
) {
    val sumLo = leftLow.toULong() + rightLow.toULong()
    val carry = if (sumLo < leftLow.toULong()) 1UL else 0UL
    val sumHi = leftHigh.toULong() + rightHigh.toULong() + carry
    vstack.setFrameSlot(destinationLowSlot, sumLo.toLong())
    vstack.setFrameSlot(destinationHighSlot, sumHi.toLong())
}

internal fun executeI64Sub128(
    vstack: ValueStack,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
    leftLow: Long,
    leftHigh: Long,
    rightLow: Long,
    rightHigh: Long,
) {
    val leftLowUnsigned = leftLow.toULong()
    val rightLowUnsigned = rightLow.toULong()
    val borrow = if (leftLowUnsigned < rightLowUnsigned) 1UL else 0UL
    val diffLo = leftLowUnsigned - rightLowUnsigned
    val diffHi = leftHigh.toULong() - rightHigh.toULong() - borrow
    vstack.setFrameSlot(destinationLowSlot, diffLo.toLong())
    vstack.setFrameSlot(destinationHighSlot, diffHi.toLong())
}

internal fun executeI64MulWideSigned(
    vstack: ValueStack,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
    left: Long,
    right: Long,
) {
    val leftUnsigned = left.toULong()
    val rightUnsigned = right.toULong()
    val (lowUnsigned, highUnsignedBase) = strictMulWideUnsigned(leftUnsigned, rightUnsigned)
    var highUnsigned = highUnsignedBase
    if (left < 0) highUnsigned -= rightUnsigned
    if (right < 0) highUnsigned -= leftUnsigned
    vstack.setFrameSlot(destinationLowSlot, lowUnsigned.toLong())
    vstack.setFrameSlot(destinationHighSlot, highUnsigned.toLong())
}

internal fun executeI64MulWideUnsigned(
    vstack: ValueStack,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
    left: Long,
    right: Long,
) {
    val (low, high) = strictMulWideUnsigned(left.toULong(), right.toULong())
    vstack.setFrameSlot(destinationLowSlot, low.toLong())
    vstack.setFrameSlot(destinationHighSlot, high.toLong())
}

internal fun strictMulWideUnsigned(a: ULong, b: ULong): Pair<ULong, ULong> {
    val mask32 = 0xFFFFFFFFUL
    val aLo = a and mask32
    val aHi = a shr 32
    val bLo = b and mask32
    val bHi = b shr 32
    val loLo = aLo * bLo
    val loHi = loLo shr 32
    val mid1 = aHi * bLo
    val mid2 = aLo * bHi
    val sum = loHi + (mid1 and mask32) + (mid2 and mask32)
    val low = (loLo and mask32) or ((sum and mask32) shl 32)
    val high = (aHi * bHi) + (mid1 shr 32) + (mid2 shr 32) + (sum shr 32)
    return Pair(low, high)
}
