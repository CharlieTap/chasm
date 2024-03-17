package io.github.charlietap.chasm.decoder.wasm.decoder.section.memory

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.MemorySection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal fun interface MemorySectionDecoder : (WasmBinaryReader, SectionSize) -> Result<MemorySection, WasmDecodeError>
