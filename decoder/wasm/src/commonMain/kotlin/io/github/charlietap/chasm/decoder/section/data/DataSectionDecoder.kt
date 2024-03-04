package io.github.charlietap.chasm.decoder.section.data

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.DataSection
import io.github.charlietap.chasm.section.SectionSize

fun interface DataSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<DataSection, WasmDecodeError>
