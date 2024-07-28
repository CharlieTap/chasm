package io.github.charlietap.chasm.decoder.decoder.type.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun ArrayTypeDecoder(
    context: DecoderContext,
): Result<ArrayType, WasmDecodeError> =
    ArrayTypeDecoder(
        context = context,
        fieldTypeDecoder = ::FieldTypeDecoder,
    )

internal fun ArrayTypeDecoder(
    context: DecoderContext,
    fieldTypeDecoder: Decoder<FieldType>,
): Result<ArrayType, WasmDecodeError> = binding {

    val fieldType = fieldTypeDecoder(context).bind()

    ArrayType(fieldType)
}
