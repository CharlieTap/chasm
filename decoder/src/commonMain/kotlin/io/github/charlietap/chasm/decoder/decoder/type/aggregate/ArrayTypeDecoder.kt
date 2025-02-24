package io.github.charlietap.chasm.decoder.decoder.type.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.FieldType

internal fun ArrayTypeDecoder(
    context: DecoderContext,
): Result<ArrayType, WasmDecodeError> =
    ArrayTypeDecoder(
        context = context,
        fieldTypeDecoder = ::FieldTypeDecoder,
    )

internal inline fun ArrayTypeDecoder(
    context: DecoderContext,
    crossinline fieldTypeDecoder: Decoder<FieldType>,
): Result<ArrayType, WasmDecodeError> = binding {

    val fieldType = fieldTypeDecoder(context).bind()

    ArrayType(fieldType)
}
