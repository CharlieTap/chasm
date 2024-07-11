@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.validator.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError

internal inline fun FieldType.unpack(): ValueType = when (val storageType = this.storageType) {
    is StorageType.Value -> storageType.type
    is StorageType.Packed -> ValueType.Number(NumberType.I32)
}

internal inline fun FieldType.unpackDefault(): Result<ValueType, ModuleValidatorError> = when (val storageType = this.storageType) {
    is StorageType.Value -> when (val type = storageType.type) {
        is ValueType.Number,
        is ValueType.Vector,
        is ValueType.Bottom,
        -> Ok(type)
        is ValueType.Reference -> when (type.referenceType) {
            is ReferenceType.Ref -> Err(TypeValidatorError.TypeMismatch)
            is ReferenceType.RefNull -> Ok(type)
        }
    }
    is StorageType.Packed -> Ok(ValueType.Number(NumberType.I32))
}
