package io.github.charlietap.chasm.decoder.decoder.section.start

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.StartSection

internal fun StartSectionDecoder(
    context: DecoderContext,
): Result<StartSection, WasmDecodeError> =
    StartSectionDecoder(
        context = context,
        functionIndexDecoder = ::FunctionIndexDecoder,
    )

internal inline fun StartSectionDecoder(
    context: DecoderContext,
    crossinline functionIndexDecoder: Decoder<Index.FunctionIndex>,
): Result<StartSection, WasmDecodeError> = binding {

    val functionIndex = functionIndexDecoder(context).bind()

    StartSection(StartFunction(functionIndex))
}
