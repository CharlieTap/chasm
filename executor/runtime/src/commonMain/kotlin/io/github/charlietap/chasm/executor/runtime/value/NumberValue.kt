package io.github.charlietap.chasm.executor.runtime.value

import kotlin.jvm.JvmInline

sealed interface NumberValue<T> : ExecutionValue {

    val value: T

    @JvmInline
    value class I32(override val value: Int) : NumberValue<Int>

    @JvmInline
    value class I64(override val value: Long) : NumberValue<Long>

    @JvmInline
    value class F32(override val value: Float) : NumberValue<Float>

    @JvmInline
    value class F64(override val value: Double) : NumberValue<Double>
}
