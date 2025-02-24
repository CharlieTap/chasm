package io.github.charlietap.chasm.decoder.decoder.type.vector

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.VectorType

internal fun VectorTypeDecoder(
    context: DecoderContext,
): Result<VectorType, WasmDecodeError> = binding {
    val encoded = context.reader.ubyte().bind()
    if (encoded == VECTOR_TYPE_128) {
        VectorType.V128
    } else {
        Err(TypeDecodeError.InvalidVectorType(encoded)).bind<VectorType>()
    }
}

internal const val VECTOR_TYPE_128: UByte = 0x7Bu
