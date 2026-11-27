package io.github.charlietap.chasm.runtime.value

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.jvm.JvmInline

@InternalChasmApi
sealed interface FieldValue {

    @JvmInline
    value class Execution(val executionValue: ExecutionValue) : FieldValue

    @JvmInline
    value class Packed(val packedValue: PackedValue) : FieldValue
}
