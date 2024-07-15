package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun StructTypeDecoder(
    context: DecoderContext,
): Result<StructType, WasmDecodeError> =
    StructTypeDecoder(
        context = context,
        fieldTypeDecoder = ::FieldTypeDecoder,
        vectorDecoder = ::VectorDecoder,
    )

internal fun StructTypeDecoder(
    context: DecoderContext,
    fieldTypeDecoder: Decoder<FieldType>,
    vectorDecoder: VectorDecoder<FieldType>,
): Result<StructType, WasmDecodeError> = binding {

    val fieldTypes = vectorDecoder(context, fieldTypeDecoder).bind()

    StructType(fieldTypes.vector)
}
