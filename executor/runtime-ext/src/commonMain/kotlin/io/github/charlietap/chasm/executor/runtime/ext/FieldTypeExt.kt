package io.github.charlietap.chasm.executor.runtime.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.PackedType
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

fun FieldType.default(): Result<ExecutionValue, InvocationError> = when (val storageType = this.storageType) {
    is StorageType.Packed -> Ok(storageType.type.default())
    is StorageType.Value -> storageType.type.default()
}

fun FieldType.bitWidth(): Result<Int, InvocationError> {
    return when (val storageType = this.storageType) {
        is StorageType.Packed -> when (storageType.type) {
            is PackedType.I8 -> 8
            is PackedType.I16 -> 16
        }

        is StorageType.Value -> when (val valueType = storageType.type) {
            is ValueType.Number -> when (valueType.numberType) {
                NumberType.I32,
                NumberType.F32,
                -> 32

                NumberType.I64,
                NumberType.F64,
                -> 64
            }

            is ValueType.Vector -> 128
            is ValueType.Reference,
            is ValueType.Bottom,
            -> return Err(InvocationError.UnobservableBitWidth)
        }
    }.let(::Ok)
}
