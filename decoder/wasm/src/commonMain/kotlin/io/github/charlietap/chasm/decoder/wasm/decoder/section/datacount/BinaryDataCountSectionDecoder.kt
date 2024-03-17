package io.github.charlietap.chasm.decoder.wasm.decoder.section.datacount

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.DataCountSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal class BinaryDataCountSectionDecoder : DataCountSectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<DataCountSection, WasmDecodeError> = binding {
        DataCountSection(reader.uint().bind())
    }
}
