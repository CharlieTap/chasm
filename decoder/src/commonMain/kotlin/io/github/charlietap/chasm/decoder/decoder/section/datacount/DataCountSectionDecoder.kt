package io.github.charlietap.chasm.decoder.decoder.section.datacount

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.DataCountSection

internal fun DataCountSectionDecoder(
    context: DecoderContext,
): Result<DataCountSection, WasmDecodeError> = binding {
    DataCountSection(context.reader.uint().bind())
}
