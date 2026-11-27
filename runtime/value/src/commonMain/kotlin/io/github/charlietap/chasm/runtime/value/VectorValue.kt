package io.github.charlietap.chasm.runtime.value

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.jvm.JvmInline

@InternalChasmApi
sealed interface VectorValue : ExecutionValue {

    @JvmInline
    value class V128(val v128: ByteArray) : VectorValue
}
