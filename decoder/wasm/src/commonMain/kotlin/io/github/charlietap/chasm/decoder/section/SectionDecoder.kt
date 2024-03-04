package io.github.charlietap.chasm.decoder.section

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.Section
import io.github.charlietap.chasm.section.SectionSize
import io.github.charlietap.chasm.section.SectionType

fun interface SectionDecoder : (WasmBinaryReader, SectionType, SectionSize) -> Result<Section, WasmDecodeError>
