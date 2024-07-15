package io.github.charlietap.chasm.decoder.wasm.decoder.section.start

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.section.StartSection

internal fun StartSectionDecoder(
    context: DecoderContext,
): Result<StartSection, WasmDecodeError> =
    StartSectionDecoder(
        context = context,
        functionIndexDecoder = ::FunctionIndexDecoder,
    )

internal fun StartSectionDecoder(
    context: DecoderContext,
    functionIndexDecoder: Decoder<Index.FunctionIndex>,
): Result<StartSection, WasmDecodeError> = binding {

    val functionIndex = functionIndexDecoder(context).bind()

    StartSection(StartFunction(functionIndex))
}
