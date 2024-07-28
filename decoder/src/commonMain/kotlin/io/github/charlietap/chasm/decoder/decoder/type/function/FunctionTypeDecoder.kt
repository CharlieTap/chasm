package io.github.charlietap.chasm.decoder.decoder.type.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.result.ResultTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun FunctionTypeDecoder(
    context: DecoderContext,
): Result<FunctionType, WasmDecodeError> = FunctionTypeDecoder(
    context,
    ::ResultTypeDecoder,
)

internal fun FunctionTypeDecoder(
    context: DecoderContext,
    resultTypeDecoder: Decoder<ResultType>,
): Result<FunctionType, WasmDecodeError> = binding {

    val params = resultTypeDecoder(context).bind()
    val results = resultTypeDecoder(context).bind()

    FunctionType(params, results)
}
