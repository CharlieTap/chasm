package io.github.charlietap.chasm.decoder.type.vector

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.error.TypeDecodeError

fun BinaryVectorTypeDecoder(encoded: UByte): Result<VectorType, TypeDecodeError.InvalidVectorType> =
    if (encoded == 0x7B.toUByte()) {
        Ok(VectorType.V128)
    } else {
        Err(TypeDecodeError.InvalidVectorType(encoded))
    }
