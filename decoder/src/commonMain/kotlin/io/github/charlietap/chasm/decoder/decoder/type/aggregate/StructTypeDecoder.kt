package io.github.charlietap.chasm.decoder.decoder.type.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun StructTypeDecoder(
    context: DecoderContext,
): Result<StructType, WasmDecodeError> =
    StructTypeDecoder(
        context = context,
        fieldTypeDecoder = ::FieldTypeDecoder,
        vectorDecoder = ::VectorDecoder,
    )

internal inline fun StructTypeDecoder(
    context: DecoderContext,
    noinline fieldTypeDecoder: Decoder<FieldType>,
    crossinline vectorDecoder: VectorDecoder<FieldType>,
): Result<StructType, WasmDecodeError> = binding {

    val fieldTypes = vectorDecoder(context, fieldTypeDecoder).bind()

    StructType(fieldTypes.vector)
}
