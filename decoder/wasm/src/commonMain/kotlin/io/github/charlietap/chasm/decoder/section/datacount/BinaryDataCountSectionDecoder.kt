package io.github.charlietap.chasm.decoder.section.datacount

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.DataCountSection
import io.github.charlietap.chasm.section.SectionSize

class BinaryDataCountSectionDecoder : DataCountSectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<DataCountSection, WasmDecodeError> = binding {
        DataCountSection(reader.uint().bind())
    }
}
