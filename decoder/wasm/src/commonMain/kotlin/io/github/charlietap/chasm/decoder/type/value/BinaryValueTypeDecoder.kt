package io.github.charlietap.chasm.decoder.type.value

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.error.TypeDecodeError
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryValueTypeDecoder(
    reader: WasmBinaryReader,
): Result<ValueType, WasmDecodeError> = binding {
    when (val byte = reader.ubyte().bind()) {
        VALUE_TYPE_NUMBER_I32 -> Ok(ValueType.Number(NumberType.I32))
        VALUE_TYPE_NUMBER_I64 -> Ok(ValueType.Number(NumberType.I64))
        VALUE_TYPE_NUMBER_F32 -> Ok(ValueType.Number(NumberType.F32))
        VALUE_TYPE_NUMBER_F64 -> Ok(ValueType.Number(NumberType.F64))
        VALUE_TYPE_VECTOR_V128 -> Ok(ValueType.Vector(VectorType.V128))
        VALUE_TYPE_REFERENCE_FUNCREF -> Ok(ValueType.Reference(ReferenceType.Funcref))
        VALUE_TYPE_REFERENCE_EXTERNREF -> Ok(ValueType.Reference(ReferenceType.Externref))
        else -> Err(TypeDecodeError.InvalidValueType(byte))
    }.bind()
}

internal const val VALUE_TYPE_NUMBER_I32: UByte = 0x7Fu
internal const val VALUE_TYPE_NUMBER_I64: UByte = 0x7Eu
internal const val VALUE_TYPE_NUMBER_F32: UByte = 0x7Du
internal const val VALUE_TYPE_NUMBER_F64: UByte = 0x7Cu

internal const val VALUE_TYPE_VECTOR_V128: UByte = 0x7Bu

internal const val VALUE_TYPE_REFERENCE_FUNCREF: UByte = 0x70u
internal const val VALUE_TYPE_REFERENCE_EXTERNREF: UByte = 0x6Fu
