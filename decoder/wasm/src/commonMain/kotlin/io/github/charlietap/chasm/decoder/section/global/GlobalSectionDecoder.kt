package io.github.charlietap.chasm.decoder.section.global

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.GlobalSection
import io.github.charlietap.chasm.section.SectionSize

fun interface GlobalSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<GlobalSection, WasmDecodeError>
