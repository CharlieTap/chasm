package io.github.charlietap.chasm.decoder.section.code

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.CodeSection
import io.github.charlietap.chasm.section.SectionSize

fun interface CodeSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<CodeSection, WasmDecodeError>
