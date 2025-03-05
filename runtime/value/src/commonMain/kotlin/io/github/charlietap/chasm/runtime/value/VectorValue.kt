package io.github.charlietap.chasm.runtime.value

import kotlin.jvm.JvmInline

sealed interface VectorValue : ExecutionValue {

    @JvmInline
    value class V128(val v128: ByteArray) : VectorValue
}
