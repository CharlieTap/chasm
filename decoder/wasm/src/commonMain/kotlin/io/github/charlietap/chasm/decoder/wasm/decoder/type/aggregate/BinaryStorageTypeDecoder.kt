package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.BinaryValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.NUMBER_TYPE_RANGE
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.REFERENCE_TYPE_RANGE
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.VECTOR_TYPE_RANGE
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryStorageTypeDecoder(
    reader: WasmBinaryReader,
): Result<StorageType, WasmDecodeError> =
    BinaryStorageTypeDecoder(
        reader = reader,
        packedTypeDecoder = ::BinaryPackedTypeDecoder,
        valueTypeDecoder = ::BinaryValueTypeDecoder,
    )

internal fun BinaryStorageTypeDecoder(
    reader: WasmBinaryReader,
    packedTypeDecoder: PackedTypeDecoder,
    valueTypeDecoder: ValueTypeDecoder,
): Result<StorageType, WasmDecodeError> = binding {
    when (val encoded = reader.peek().ubyte().bind()) {
        in NUMBER_TYPE_RANGE,
        in VECTOR_TYPE_RANGE,
        in REFERENCE_TYPE_RANGE,
        -> {
            val valueType = valueTypeDecoder(reader).bind()
            StorageType.Value(valueType)
        }
        in PACKED_TYPE_RANGE -> {
            val packedType = packedTypeDecoder(reader).bind()
            StorageType.Packed(packedType)
        }
        else -> Err(TypeDecodeError.InvalidStorageType(encoded)).bind<StorageType>()
    }
}
