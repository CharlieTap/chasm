package io.github.charlietap.chasm.executor.memory.ext

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
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.VectorValue

fun FieldType.valueFromBytes(bytes: UByteArray): Result<ExecutionValue, InvocationError> {
    return when (val storageType = this.storageType) {
        is StorageType.Packed -> when (storageType.type) {
            is PackedType.I8 -> NumberValue.I32(bytes.first().toInt())
            is PackedType.I16 -> NumberValue.I32(bytes.asByteArray().toShortLittleEndian().toInt())
        }

        is StorageType.Value -> when (val valueType = storageType.type) {
            is ValueType.Number -> when (valueType.numberType) {
                NumberType.I32 -> NumberValue.I32(bytes.asByteArray().toIntLittleEndian())
                NumberType.I64 -> NumberValue.I64(bytes.asByteArray().toLongLittleEndian())
                NumberType.F32 -> NumberValue.F32(bytes.asByteArray().toFloatLittleEndian())
                NumberType.F64 -> NumberValue.F64(bytes.asByteArray().toDoubleLittleEndian())
            }

            is ValueType.Vector -> VectorValue.V128(bytes.asByteArray())
            is ValueType.Bottom,
            is ValueType.Reference,
            -> return Err(InvocationError.UnobservableBitWidth)
        }
    }.let(::Ok)
}
