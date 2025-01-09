package io.github.charlietap.chasm.executor.runtime.value

import kotlin.jvm.JvmInline

sealed interface FieldValue {

    @JvmInline
    value class Execution(val executionValue: ExecutionValue) : FieldValue

    @JvmInline
    value class Packed(val packedValue: PackedValue) : FieldValue
}
