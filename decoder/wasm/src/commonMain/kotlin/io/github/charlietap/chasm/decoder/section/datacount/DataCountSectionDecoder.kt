package io.github.charlietap.chasm.decoder.section.datacount

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.DataCountSection
import io.github.charlietap.chasm.section.SectionSize

fun interface DataCountSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<DataCountSection, WasmDecodeError>
