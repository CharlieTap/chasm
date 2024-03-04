package io.github.charlietap.chasm.decoder.section.custom

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.CustomSection
import io.github.charlietap.chasm.section.SectionSize

fun interface CustomSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<CustomSection, WasmDecodeError>
