package io.github.charlietap.chasm.decoder.section.memory

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.MemorySection
import io.github.charlietap.chasm.section.SectionSize

fun interface MemorySectionDecoder : (WasmBinaryReader, SectionSize) -> Result<MemorySection, WasmDecodeError>
