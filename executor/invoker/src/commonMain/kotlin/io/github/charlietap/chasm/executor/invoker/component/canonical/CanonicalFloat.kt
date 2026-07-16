package io.github.charlietap.chasm.executor.invoker.component.canonical

internal fun CanonicalFloat(value: Float): Float =
    if (value.isNaN()) Float.fromBits(CANONICAL_FLOAT_NAN) else value

internal fun CanonicalDouble(value: Double): Double =
    if (value.isNaN()) Double.fromBits(CANONICAL_DOUBLE_NAN) else value

private const val CANONICAL_FLOAT_NAN = 0x7fc00000
private const val CANONICAL_DOUBLE_NAN = 0x7ff8000000000000L
