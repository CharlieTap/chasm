package io.github.charlietap.chasm.executor.runtime.encoder

import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun ReferenceValue.toLong(): Long = ReferenceValueEncoder(this)

fun Long.toReferenceValue(): ReferenceValue = ReferenceValueDecoder(this)

inline fun Long.isNullableReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_NULL
