@file:JvmName("CompiledNumerics")

package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.InternalChasmApi
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

@InternalChasmApi
fun i32DivS(
    left: Int,
    right: Int,
): Int = strictI32DivS(left, right)

@InternalChasmApi
fun i32DivU(
    left: Int,
    right: Int,
): Int = strictI32DivU(left, right)

@InternalChasmApi
fun i32RemS(
    left: Int,
    right: Int,
): Int = strictI32RemS(left, right)

@InternalChasmApi
fun i32RemU(
    left: Int,
    right: Int,
): Int = strictI32RemU(left, right)

@InternalChasmApi
fun i64DivS(
    left: Long,
    right: Long,
): Long = strictI64DivS(left, right)

@InternalChasmApi
fun i64DivU(
    left: Long,
    right: Long,
): Long = strictI64DivU(left, right)

@InternalChasmApi
fun i64RemS(
    left: Long,
    right: Long,
): Long = strictI64RemS(left, right)

@InternalChasmApi
fun i64RemU(
    left: Long,
    right: Long,
): Long = strictI64RemU(left, right)

@InternalChasmApi
fun f32Abs(
    operand: Float,
): Float = operand.absoluteValue

@InternalChasmApi
fun f32Ceil(
    operand: Float,
): Float = operand.ceil()

@InternalChasmApi
fun f32Floor(
    operand: Float,
): Float = operand.floor()

@InternalChasmApi
fun f32Trunc(
    operand: Float,
): Float = operand.trunc()

@InternalChasmApi
fun f32Nearest(
    operand: Float,
): Float = operand.nearest()

@InternalChasmApi
fun f32Sqrt(
    operand: Float,
): Float = operand.sqrt()

@InternalChasmApi
fun f32Min(
    left: Float,
    right: Float,
): Float = left.min(right)

@InternalChasmApi
fun f32Max(
    left: Float,
    right: Float,
): Float = left.max(right)

@InternalChasmApi
fun f32Copysign(
    left: Float,
    right: Float,
): Float = left.copySign(right)

@InternalChasmApi
fun f64Abs(
    operand: Double,
): Double = operand.absoluteValue

@InternalChasmApi
fun f64Ceil(
    operand: Double,
): Double = operand.ceil()

@InternalChasmApi
fun f64Floor(
    operand: Double,
): Double = operand.floor()

@InternalChasmApi
fun f64Trunc(
    operand: Double,
): Double = operand.trunc()

@InternalChasmApi
fun f64Nearest(
    operand: Double,
): Double = operand.nearest()

@InternalChasmApi
fun f64Sqrt(
    operand: Double,
): Double = operand.sqrt()

@InternalChasmApi
fun f64Min(
    left: Double,
    right: Double,
): Double = left.min(right)

@InternalChasmApi
fun f64Max(
    left: Double,
    right: Double,
): Double = left.max(right)

@InternalChasmApi
fun f64Copysign(
    left: Double,
    right: Double,
): Double = left.copySign(right)

@InternalChasmApi
fun i32TruncF32S(
    operand: Float,
): Int = converting { operand.truncI32sTrapping() }

@InternalChasmApi
fun i32TruncF32U(
    operand: Float,
): Int = converting { operand.truncI32uTrapping() }

@InternalChasmApi
fun i32TruncF64S(
    operand: Double,
): Int = converting { operand.truncI32sTrapping() }

@InternalChasmApi
fun i32TruncF64U(
    operand: Double,
): Int = converting { operand.truncI32uTrapping() }

@InternalChasmApi
fun i64TruncF32S(
    operand: Float,
): Long = converting { operand.truncI64sTrapping() }

@InternalChasmApi
fun i64TruncF32U(
    operand: Float,
): Long = converting { operand.truncI64uTrapping() }

@InternalChasmApi
fun i64TruncF64S(
    operand: Double,
): Long = converting { operand.truncI64sTrapping() }

@InternalChasmApi
fun i64TruncF64U(
    operand: Double,
): Long = converting { operand.truncI64uTrapping() }

@InternalChasmApi
fun i32TruncSatF32S(
    operand: Float,
): Int = operand.truncI32s()

@InternalChasmApi
fun i32TruncSatF32U(
    operand: Float,
): Int = operand.truncI32u()

@InternalChasmApi
fun i32TruncSatF64S(
    operand: Double,
): Int = operand.truncI32s()

@InternalChasmApi
fun i32TruncSatF64U(
    operand: Double,
): Int = operand.truncI32u()

@InternalChasmApi
fun i64TruncSatF32S(
    operand: Float,
): Long = operand.truncI64s()

@InternalChasmApi
fun i64TruncSatF32U(
    operand: Float,
): Long = operand.truncI64u()

@InternalChasmApi
fun i64TruncSatF64S(
    operand: Double,
): Long = operand.truncI64s()

@InternalChasmApi
fun i64TruncSatF64U(
    operand: Double,
): Long = operand.truncI64u()

@InternalChasmApi
fun f32ConvertI32U(
    operand: Int,
): Float = operand.convertF32u()

@InternalChasmApi
fun f32ConvertI64U(
    operand: Long,
): Float = operand.convertF32u()

@InternalChasmApi
fun f64ConvertI32U(
    operand: Int,
): Double = operand.convertF64u()

@InternalChasmApi
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

@InternalChasmApi
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

@InternalChasmApi
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
