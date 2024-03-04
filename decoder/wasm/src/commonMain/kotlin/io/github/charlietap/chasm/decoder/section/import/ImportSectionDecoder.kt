package io.github.charlietap.chasm.decoder.section.import

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.ImportSection
import io.github.charlietap.chasm.section.SectionSize

fun interface ImportSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<ImportSection, WasmDecodeError>
