package io.github.charlietap.chasm.decoder.section.element

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.ElementSection
import io.github.charlietap.chasm.section.SectionSize

fun interface ElementSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<ElementSection, WasmDecodeError>
