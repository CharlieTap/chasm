package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.BinaryMutabilityDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.MutabilityDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryFieldTypeDecoder(
    reader: WasmBinaryReader,
): Result<FieldType, WasmDecodeError> =
    BinaryFieldTypeDecoder(
        reader = reader,
        storageTypeDecoder = ::BinaryStorageTypeDecoder,
        mutabilityDecoder = ::BinaryMutabilityDecoder,
    )

internal fun BinaryFieldTypeDecoder(
    reader: WasmBinaryReader,
    storageTypeDecoder: StorageTypeDecoder,
    mutabilityDecoder: MutabilityDecoder,
): Result<FieldType, WasmDecodeError> = binding {
    val storageType = storageTypeDecoder(reader).bind()
    val mutability = mutabilityDecoder(reader).bind()

    FieldType(storageType, mutability)
}
