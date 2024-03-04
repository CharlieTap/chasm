package io.github.charlietap.chasm.decoder.section.function

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.FunctionSection
import io.github.charlietap.chasm.section.SectionSize

fun interface FunctionSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<FunctionSection, WasmDecodeError>
