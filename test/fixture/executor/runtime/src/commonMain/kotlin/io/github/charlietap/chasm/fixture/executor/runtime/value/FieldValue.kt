package io.github.charlietap.chasm.fixture.executor.runtime.value

import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.FieldValue
import io.github.charlietap.chasm.executor.runtime.value.PackedValue

fun fieldValue(): FieldValue = executionFieldValue()

fun executionFieldValue(
    executionValue: ExecutionValue = executionValue(),
): FieldValue.Execution = FieldValue.Execution(executionValue)

fun packedFieldValue(
    packedValue: PackedValue = packedValue(),
): FieldValue.Packed = FieldValue.Packed(packedValue)
