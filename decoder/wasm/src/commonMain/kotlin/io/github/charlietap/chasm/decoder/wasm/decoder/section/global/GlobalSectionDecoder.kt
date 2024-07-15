package io.github.charlietap.chasm.decoder.wasm.decoder.section.global

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.section.GlobalSection

internal fun GlobalSectionDecoder(
    context: DecoderContext,
): Result<GlobalSection, WasmDecodeError> =
    GlobalSectionDecoder(
        context = context,
        vectorDecoder = ::VectorDecoder,
        globalDecoder = ::GlobalDecoder,
    )

internal fun GlobalSectionDecoder(
    context: DecoderContext,
    vectorDecoder: VectorDecoder<Global>,
    globalDecoder: Decoder<Global>,
): Result<GlobalSection, WasmDecodeError> = binding {

    val globals = vectorDecoder(context, globalDecoder).bind().vector.mapIndexed { idx, global ->
        global.copy(idx = Index.GlobalIndex(idx.toUInt()))
    }

    GlobalSection(globals)
}
