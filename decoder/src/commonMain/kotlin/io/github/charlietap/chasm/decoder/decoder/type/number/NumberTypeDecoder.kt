package io.github.charlietap.chasm.decoder.decoder.type.number

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun NumberTypeDecoder(
    context: DecoderContext,
): Result<NumberType, WasmDecodeError> = binding {
    when (val encoded = context.reader.ubyte().bind()) {
        NUMBER_TYPE_I32 -> NumberType.I32
        NUMBER_TYPE_I64 -> NumberType.I64
        NUMBER_TYPE_F32 -> NumberType.F32
        NUMBER_TYPE_F64 -> NumberType.F64
        else -> Err(TypeDecodeError.InvalidNumberType(encoded)).bind<NumberType>()
    }
}

internal const val NUMBER_TYPE_I32: UByte = 0x7Fu
internal const val NUMBER_TYPE_I64: UByte = 0x7Eu
internal const val NUMBER_TYPE_F32: UByte = 0x7Du
internal const val NUMBER_TYPE_F64: UByte = 0x7Cu
