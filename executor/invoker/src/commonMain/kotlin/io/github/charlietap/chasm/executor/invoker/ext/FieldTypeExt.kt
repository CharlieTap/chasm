package io.github.charlietap.chasm.executor.invoker.ext

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.PackedType
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.executor.memory.ext.toDoubleLittleEndian
import io.github.charlietap.chasm.executor.memory.ext.toFloatLittleEndian
import io.github.charlietap.chasm.executor.memory.ext.toIntLittleEndian
import io.github.charlietap.chasm.executor.memory.ext.toLongLittleEndian
import io.github.charlietap.chasm.executor.memory.ext.toShortLittleEndian
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.VectorValue

fun FieldType.valueFromBytes(bytes: UByteArray): ExecutionValue {
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
            -> throw InvocationException(InvocationError.UnobservableBitWidth)
        }
    }
}

fun FieldType.valueFromBytes(
    bytes: UByteArray,
    offset: Int,
): ExecutionValue {
    return when (val storageType = this.storageType) {
        is StorageType.Packed -> when (storageType.type) {
            is PackedType.I8 -> NumberValue.I32(bytes[offset].toInt())
            is PackedType.I16 -> NumberValue.I32(bytes.asByteArray().toShortLittleEndian(offset).toInt())
        }
        is StorageType.Value -> when (val valueType = storageType.type) {
            is ValueType.Number -> when (valueType.numberType) {
                NumberType.I32 -> NumberValue.I32(bytes.asByteArray().toIntLittleEndian(offset))
                NumberType.I64 -> NumberValue.I64(bytes.asByteArray().toLongLittleEndian(offset))
                NumberType.F32 -> NumberValue.F32(bytes.asByteArray().toFloatLittleEndian(offset))
                NumberType.F64 -> NumberValue.F64(bytes.asByteArray().toDoubleLittleEndian(offset))
            }
            is ValueType.Bottom,
            is ValueType.Reference,
            is ValueType.Vector,
            -> throw InvocationException(InvocationError.UnobservableBitWidth)
        }
    }
}
