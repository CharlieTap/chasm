package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.PackedType
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryPackedTypeDecoder(
    reader: WasmBinaryReader,
): Result<PackedType, WasmDecodeError> = binding {
    when (val encoded = reader.ubyte().bind()) {
        PACKED_TYPE_I8 -> PackedType.I8
        PACKED_TYPE_I16 -> PackedType.I16
        else -> Err(TypeDecodeError.InvalidPackedType(encoded)).bind<PackedType>()
    }
}

internal const val PACKED_TYPE_I8: UByte = 0x78u
internal const val PACKED_TYPE_I16: UByte = 0x77u

internal val PACKED_TYPE_RANGE = PACKED_TYPE_I16..PACKED_TYPE_I8
