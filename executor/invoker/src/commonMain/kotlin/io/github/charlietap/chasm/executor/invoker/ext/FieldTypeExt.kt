package io.github.charlietap.chasm.executor.invoker.ext

import io.github.charlietap.chasm.executor.memory.ext.toDoubleLittleEndian
import io.github.charlietap.chasm.executor.memory.ext.toFloatLittleEndian
import io.github.charlietap.chasm.executor.memory.ext.toIntLittleEndian
import io.github.charlietap.chasm.executor.memory.ext.toLongLittleEndian
import io.github.charlietap.chasm.executor.memory.ext.toShortLittleEndian
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType

fun FieldType.valueFromBytes(
    bytes: UByteArray,
    offset: Int,
): Long {
    return when (val storageType = this.storageType) {
        is StorageType.Packed -> when (storageType.type) {
            is PackedType.I8 -> bytes[offset].toLong()
            is PackedType.I16 -> bytes.asByteArray().toShortLittleEndian(offset).toLong()
        }
        is StorageType.Value -> when (val valueType = storageType.type) {
            is ValueType.Number -> when (valueType.numberType) {
                NumberType.I32 -> bytes.asByteArray().toIntLittleEndian(offset).toLong()
                NumberType.I64 -> bytes.asByteArray().toLongLittleEndian(offset)
                NumberType.F32 -> bytes.asByteArray().toFloatLittleEndian(offset).toLong()
                NumberType.F64 -> bytes.asByteArray().toDoubleLittleEndian(offset).toLong()
            }
            is ValueType.Bottom,
            is ValueType.Reference,
            is ValueType.Vector,
            -> throw InvocationException(InvocationError.UnobservableBitWidth)
        }
    }
}
