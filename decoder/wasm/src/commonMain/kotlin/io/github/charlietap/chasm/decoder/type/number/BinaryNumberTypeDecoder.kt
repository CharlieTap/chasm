package io.github.charlietap.chasm.decoder.type.number

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.error.TypeDecodeError

fun BinaryNumberTypeDecoder(
    encoded: UByte,
): Result<NumberType, TypeDecodeError.InvalidNumberType> = when (encoded) {
    0x7F.toUByte() -> Ok(NumberType.I32)
    0x7E.toUByte() -> Ok(NumberType.I64)
    0x7D.toUByte() -> Ok(NumberType.F32)
    0x7C.toUByte() -> Ok(NumberType.F64)
    else -> Err(TypeDecodeError.InvalidNumberType(encoded))
}
