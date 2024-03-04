package io.github.charlietap.chasm.decoder.type.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.error.TypeDecodeError

fun BinaryReferenceTypeDecoder(
    encoded: UByte,
): Result<ReferenceType, TypeDecodeError.InvalidReferenceType> = when (encoded) {
    0x70.toUByte() -> Ok(ReferenceType.Funcref)
    0x6F.toUByte() -> Ok(ReferenceType.Externref)
    else -> Err(TypeDecodeError.InvalidReferenceType(encoded))
}
