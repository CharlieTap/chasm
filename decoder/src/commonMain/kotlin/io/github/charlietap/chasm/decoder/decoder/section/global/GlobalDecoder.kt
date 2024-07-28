package io.github.charlietap.chasm.decoder.decoder.section.global

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.decoder.type.global.GlobalTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun GlobalDecoder(
    context: DecoderContext,
): Result<Global, WasmDecodeError> = GlobalDecoder(
    context = context,
    globalTypeDecoder = ::GlobalTypeDecoder,
    expressionDecoder = ::ExpressionDecoder,
)

internal fun GlobalDecoder(
    context: DecoderContext,
    globalTypeDecoder: Decoder<GlobalType>,
    expressionDecoder: Decoder<Expression>,
): Result<Global, WasmDecodeError> = binding {
    val type = globalTypeDecoder(context).bind()
    val expression = expressionDecoder(context).bind()
    Global(Index.GlobalIndex(0u), type, expression)
}
