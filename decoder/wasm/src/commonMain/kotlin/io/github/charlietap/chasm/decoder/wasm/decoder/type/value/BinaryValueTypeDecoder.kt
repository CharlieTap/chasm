package io.github.charlietap.chasm.decoder.wasm.decoder.type.value

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.BinaryReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryValueTypeDecoder(
    reader: WasmBinaryReader,
): Result<ValueType, WasmDecodeError> =
    BinaryValueTypeDecoder(
        reader = reader,
        referenceTypeDecoder = ::BinaryReferenceTypeDecoder,
    )

internal fun BinaryValueTypeDecoder(
    reader: WasmBinaryReader,
    referenceTypeDecoder: ReferenceTypeDecoder,
): Result<ValueType, WasmDecodeError> = binding {
    when (val byte = reader.ubyte().bind()) {
        VALUE_TYPE_NUMBER_I32 -> ValueType.Number(NumberType.I32)
        VALUE_TYPE_NUMBER_I64 -> ValueType.Number(NumberType.I64)
        VALUE_TYPE_NUMBER_F32 -> ValueType.Number(NumberType.F32)
        VALUE_TYPE_NUMBER_F64 -> ValueType.Number(NumberType.F64)
        VALUE_TYPE_VECTOR_V128 -> ValueType.Vector(VectorType.V128)
        VALUE_TYPE_REFERENCE_FUNCREF -> ValueType.Reference(ReferenceType.RefNull(HeapType.Func))
        VALUE_TYPE_REFERENCE_EXTERNREF -> ValueType.Reference(ReferenceType.RefNull(HeapType.Extern))
        VALUE_TYPE_REFERENCE_REF -> ValueType.Reference(referenceTypeDecoder(reader, byte).bind())
        VALUE_TYPE_REFERENCE_REF_NULL -> ValueType.Reference(referenceTypeDecoder(reader, byte).bind())
        else -> Err(TypeDecodeError.InvalidValueType(byte)).bind<ValueType>()
    }
}

internal const val VALUE_TYPE_NUMBER_I32: UByte = 0x7Fu
internal const val VALUE_TYPE_NUMBER_I64: UByte = 0x7Eu
internal const val VALUE_TYPE_NUMBER_F32: UByte = 0x7Du
internal const val VALUE_TYPE_NUMBER_F64: UByte = 0x7Cu

internal const val VALUE_TYPE_VECTOR_V128: UByte = 0x7Bu

internal const val VALUE_TYPE_REFERENCE_FUNCREF: UByte = 0x70u
internal const val VALUE_TYPE_REFERENCE_EXTERNREF: UByte = 0x6Fu
internal const val VALUE_TYPE_REFERENCE_REF_NULL: UByte = 0x63u
internal const val VALUE_TYPE_REFERENCE_REF: UByte = 0x64u
