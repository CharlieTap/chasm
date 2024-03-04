package io.github.charlietap.chasm.decoder.section.type

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.SectionSize
import io.github.charlietap.chasm.section.TypeSection

fun interface TypeSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<TypeSection, WasmDecodeError>
