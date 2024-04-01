package io.github.charlietap.chasm.decoder.wasm.decoder.type.number

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError

internal fun BinaryNumberTypeDecoder(
    encoded: UByte,
): Result<NumberType, TypeDecodeError.InvalidNumberType> = when (encoded) {
    NUMBER_TYPE_I32 -> Ok(NumberType.I32)
    NUMBER_TYPE_I64 -> Ok(NumberType.I64)
    NUMBER_TYPE_F32 -> Ok(NumberType.F32)
    NUMBER_TYPE_F64 -> Ok(NumberType.F64)
    else -> Err(TypeDecodeError.InvalidNumberType(encoded))
}

internal const val NUMBER_TYPE_I32: UByte = 0x7Fu
internal const val NUMBER_TYPE_I64: UByte = 0x7Eu
internal const val NUMBER_TYPE_F32: UByte = 0x7Du
internal const val NUMBER_TYPE_F64: UByte = 0x7Cu
