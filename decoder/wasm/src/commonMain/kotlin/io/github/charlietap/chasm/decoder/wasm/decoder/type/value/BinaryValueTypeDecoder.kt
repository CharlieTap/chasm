package io.github.charlietap.chasm.decoder.wasm.decoder.type.value

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.HEAP_TYPE_NO_FUNC
import io.github.charlietap.chasm.decoder.wasm.decoder.type.number.BinaryNumberTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.number.NUMBER_TYPE_F64
import io.github.charlietap.chasm.decoder.wasm.decoder.type.number.NUMBER_TYPE_I32
import io.github.charlietap.chasm.decoder.wasm.decoder.type.number.NumberTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.BinaryReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.REFERENCE_TYPE_REF_NULL
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.vector.VECTOR_TYPE_128
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryValueTypeDecoder(
    reader: WasmBinaryReader,
): Result<ValueType, WasmDecodeError> =
    BinaryValueTypeDecoder(
        reader = reader,
        numberTypeDecoder = ::BinaryNumberTypeDecoder,
        referenceTypeDecoder = ::BinaryReferenceTypeDecoder,
    )

internal fun BinaryValueTypeDecoder(
    reader: WasmBinaryReader,
    numberTypeDecoder: NumberTypeDecoder,
    referenceTypeDecoder: ReferenceTypeDecoder,
): Result<ValueType, WasmDecodeError> = binding {
    when (val byte = reader.ubyte().bind()) {
        in NUMBER_TYPE_RANGE -> {
            val numberType = numberTypeDecoder(byte).bind()
            ValueType.Number(numberType)
        }
        in VECTOR_TYPE_RANGE -> {
            ValueType.Vector(VectorType.V128)
        }
        in REFERENCE_TYPE_RANGE -> {
            val referenceType = referenceTypeDecoder(reader, byte).bind()
            ValueType.Reference(referenceType)
        }
        else -> Err(TypeDecodeError.InvalidValueType(byte)).bind<ValueType>()
    }
}

internal val NUMBER_TYPE_RANGE = NUMBER_TYPE_F64..NUMBER_TYPE_I32
internal val VECTOR_TYPE_RANGE = VECTOR_TYPE_128..VECTOR_TYPE_128
internal val REFERENCE_TYPE_RANGE = REFERENCE_TYPE_REF_NULL..HEAP_TYPE_NO_FUNC
