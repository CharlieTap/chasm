package io.github.charlietap.chasm.decoder.wasm.decoder.type.vector

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError

internal fun BinaryVectorTypeDecoder(encoded: UByte): Result<VectorType, TypeDecodeError.InvalidVectorType> =
    if (encoded == VECTOR_TYPE_128) {
        Ok(VectorType.V128)
    } else {
        Err(TypeDecodeError.InvalidVectorType(encoded))
    }

internal const val VECTOR_TYPE_128: UByte = 0x7Bu
