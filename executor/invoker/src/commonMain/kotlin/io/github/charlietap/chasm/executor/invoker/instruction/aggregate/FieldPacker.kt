package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.PackedType
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.executor.runtime.ext.toExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.FieldValue
import io.github.charlietap.chasm.executor.runtime.value.PackedValue

internal typealias FieldPacker = (Long, FieldType) -> FieldValue

internal fun FieldPacker(
    value: Long,
    fieldType: FieldType,
): FieldValue {
    return when (val storageType = fieldType.storageType) {
        is StorageType.Packed -> {
            val packedValue = when (storageType.type) {
                PackedType.I8 -> {
                    PackedValue.I8(value)
                }
                PackedType.I16 -> {
                    PackedValue.I16(value)
                }
            }
            FieldValue.Packed(packedValue)
        }
        is StorageType.Value -> {
            FieldValue.Execution(value.toExecutionValue(storageType.type))
        }
    }
}
