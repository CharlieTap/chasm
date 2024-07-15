package io.github.charlietap.chasm.decoder.wasm.decoder.section.datacount

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.section.DataCountSection

internal fun DataCountSectionDecoder(
    context: DecoderContext,
): Result<DataCountSection, WasmDecodeError> = binding {
    DataCountSection(context.reader.uint().bind())
}
