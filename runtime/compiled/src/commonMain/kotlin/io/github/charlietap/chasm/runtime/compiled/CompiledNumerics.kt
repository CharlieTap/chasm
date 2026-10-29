@file:JvmName("CompiledNumerics")

package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.operations.ext.ceil
import io.github.charlietap.chasm.runtime.operations.ext.convertF32u
import io.github.charlietap.chasm.runtime.operations.ext.convertF64u
import io.github.charlietap.chasm.runtime.operations.ext.copySign
import io.github.charlietap.chasm.runtime.operations.ext.floor
import io.github.charlietap.chasm.runtime.operations.ext.max
import io.github.charlietap.chasm.runtime.operations.ext.min
import io.github.charlietap.chasm.runtime.operations.ext.nearest
import io.github.charlietap.chasm.runtime.operations.ext.sqrt
import io.github.charlietap.chasm.runtime.operations.ext.trunc
import io.github.charlietap.chasm.runtime.operations.ext.truncI32s
import io.github.charlietap.chasm.runtime.operations.ext.truncI32sTrapping
import io.github.charlietap.chasm.runtime.operations.ext.truncI32u
import io.github.charlietap.chasm.runtime.operations.ext.truncI32uTrapping
import io.github.charlietap.chasm.runtime.operations.ext.truncI64s
import io.github.charlietap.chasm.runtime.operations.ext.truncI64sTrapping
import io.github.charlietap.chasm.runtime.operations.ext.truncI64u
import io.github.charlietap.chasm.runtime.operations.ext.truncI64uTrapping
import io.github.charlietap.chasm.runtime.operations.strictI32DivS
import io.github.charlietap.chasm.runtime.operations.strictI32DivU
import io.github.charlietap.chasm.runtime.operations.strictI32RemS
import io.github.charlietap.chasm.runtime.operations.strictI32RemU
import io.github.charlietap.chasm.runtime.operations.strictI64DivS
import io.github.charlietap.chasm.runtime.operations.strictI64DivU
import io.github.charlietap.chasm.runtime.operations.strictI64RemS
import io.github.charlietap.chasm.runtime.operations.strictI64RemU
import kotlin.jvm.JvmName
import kotlin.math.absoluteValue

fun i32DivS(
    left: Int,
    right: Int,
): Int = strictI32DivS(left, right)

fun i32DivU(
    left: Int,
    right: Int,
): Int = strictI32DivU(left, right)

fun i32RemS(
    left: Int,
    right: Int,
): Int = strictI32RemS(left, right)

fun i32RemU(
    left: Int,
    right: Int,
): Int = strictI32RemU(left, right)

fun i64DivS(
    left: Long,
    right: Long,
): Long = strictI64DivS(left, right)

fun i64DivU(
    left: Long,
    right: Long,
): Long = strictI64DivU(left, right)

fun i64RemS(
    left: Long,
    right: Long,
): Long = strictI64RemS(left, right)

fun i64RemU(
    left: Long,
    right: Long,
): Long = strictI64RemU(left, right)

fun f32Abs(
    operand: Float,
): Float = operand.absoluteValue

fun f32Ceil(
    operand: Float,
): Float = operand.ceil()

fun f32Floor(
    operand: Float,
): Float = operand.floor()

fun f32Trunc(
    operand: Float,
): Float = operand.trunc()

fun f32Nearest(
    operand: Float,
): Float = operand.nearest()

fun f32Sqrt(
    operand: Float,
): Float = operand.sqrt()

fun f32Min(
    left: Float,
    right: Float,
): Float = left.min(right)

fun f32Max(
    left: Float,
    right: Float,
): Float = left.max(right)

fun f32Copysign(
    left: Float,
    right: Float,
): Float = left.copySign(right)

fun f64Abs(
    operand: Double,
): Double = operand.absoluteValue

fun f64Ceil(
    operand: Double,
): Double = operand.ceil()

fun f64Floor(
    operand: Double,
): Double = operand.floor()

fun f64Trunc(
    operand: Double,
): Double = operand.trunc()

fun f64Nearest(
    operand: Double,
): Double = operand.nearest()

fun f64Sqrt(
    operand: Double,
): Double = operand.sqrt()

fun f64Min(
    left: Double,
    right: Double,
): Double = left.min(right)

fun f64Max(
    left: Double,
    right: Double,
): Double = left.max(right)

fun f64Copysign(
    left: Double,
    right: Double,
): Double = left.copySign(right)

fun i32TruncF32S(
    operand: Float,
): Int = converting { operand.truncI32sTrapping() }

fun i32TruncF32U(
    operand: Float,
): Int = converting { operand.truncI32uTrapping() }

fun i32TruncF64S(
    operand: Double,
): Int = converting { operand.truncI32sTrapping() }

fun i32TruncF64U(
    operand: Double,
): Int = converting { operand.truncI32uTrapping() }

fun i64TruncF32S(
    operand: Float,
): Long = converting { operand.truncI64sTrapping() }

fun i64TruncF32U(
    operand: Float,
): Long = converting { operand.truncI64uTrapping() }

fun i64TruncF64S(
    operand: Double,
): Long = converting { operand.truncI64sTrapping() }

fun i64TruncF64U(
    operand: Double,
): Long = converting { operand.truncI64uTrapping() }

fun i32TruncSatF32S(
    operand: Float,
): Int = operand.truncI32s()

fun i32TruncSatF32U(
    operand: Float,
): Int = operand.truncI32u()

fun i32TruncSatF64S(
    operand: Double,
): Int = operand.truncI32s()

fun i32TruncSatF64U(
    operand: Double,
): Int = operand.truncI32u()

fun i64TruncSatF32S(
    operand: Float,
): Long = operand.truncI64s()

fun i64TruncSatF32U(
    operand: Float,
): Long = operand.truncI64u()

fun i64TruncSatF64S(
    operand: Double,
): Long = operand.truncI64s()

fun i64TruncSatF64U(
    operand: Double,
): Long = operand.truncI64u()

fun f32ConvertI32U(
    operand: Int,
): Float = operand.convertF32u()

fun f32ConvertI64U(
    operand: Long,
): Float = operand.convertF32u()

fun f64ConvertI32U(
    operand: Int,
): Double = operand.convertF64u()

fun f64ConvertI64U(
    operand: Long,
): Double = operand.convertF64u()

private inline fun <T> converting(
    conversion: () -> T,
): T = try {
    conversion()
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ConvertOperationFailed)
}

fun i64MulWideHighU(
    left: Long,
    right: Long,
): Long {
    val leftLow = left and LOW_32
    val leftHigh = left ushr 32
    val rightLow = right and LOW_32
    val rightHigh = right ushr 32
    val lowLow = leftLow * rightLow
    val highLow = leftHigh * rightLow
    val lowHigh = leftLow * rightHigh
    val highHigh = leftHigh * rightHigh
    val middle = (lowLow ushr 32) + (highLow and LOW_32) + (lowHigh and LOW_32)
    return highHigh + (highLow ushr 32) + (lowHigh ushr 32) + (middle ushr 32)
}

fun i64MulWideHighS(
    left: Long,
    right: Long,
): Long {
    var high = i64MulWideHighU(left, right)
    if (left < 0) high -= right
    if (right < 0) high -= left
    return high
}

private const val LOW_32 = 0xFFFF_FFFFL
