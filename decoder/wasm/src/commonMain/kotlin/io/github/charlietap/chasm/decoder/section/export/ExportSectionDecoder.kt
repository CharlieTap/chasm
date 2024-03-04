package io.github.charlietap.chasm.decoder.section.export

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.ExportSection
import io.github.charlietap.chasm.section.SectionSize

fun interface ExportSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<ExportSection, WasmDecodeError>
