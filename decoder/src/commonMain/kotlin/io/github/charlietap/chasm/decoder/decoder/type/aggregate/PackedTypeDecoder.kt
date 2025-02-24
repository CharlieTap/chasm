package io.github.charlietap.chasm.decoder.decoder.type.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.PackedType

internal fun PackedTypeDecoder(
    context: DecoderContext,
): Result<PackedType, WasmDecodeError> = binding {
    when (val encoded = context.reader.ubyte().bind()) {
        PACKED_TYPE_I8 -> PackedType.I8
        PACKED_TYPE_I16 -> PackedType.I16
        else -> Err(TypeDecodeError.InvalidPackedType(encoded)).bind<PackedType>()
    }
}

internal const val PACKED_TYPE_I8: UByte = 0x78u
internal const val PACKED_TYPE_I16: UByte = 0x77u

internal val PACKED_TYPE_RANGE = PACKED_TYPE_I16..PACKED_TYPE_I8
