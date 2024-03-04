package io.github.charlietap.chasm.decoder.section.start

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.SectionSize
import io.github.charlietap.chasm.section.StartSection

fun interface StartSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<StartSection, WasmDecodeError>
