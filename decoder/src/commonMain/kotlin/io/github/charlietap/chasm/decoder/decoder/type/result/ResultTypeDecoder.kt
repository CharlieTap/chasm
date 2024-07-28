package io.github.charlietap.chasm.decoder.decoder.type.result

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun ResultTypeDecoder(
    context: DecoderContext,
): Result<ResultType, WasmDecodeError> = ResultTypeDecoder(
    context,
    ::VectorDecoder,
    ::ValueTypeDecoder,
)

internal fun ResultTypeDecoder(
    context: DecoderContext,
    vectorDecoder: VectorDecoder<ValueType>,
    valueTypeDecoder: Decoder<ValueType>,
): Result<ResultType, WasmDecodeError> = binding {
    val valueTypes = vectorDecoder(context, valueTypeDecoder).bind()
    ResultType(valueTypes.vector)
}
