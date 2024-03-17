package io.github.charlietap.chasm.decoder.wasm.decoder.section.global

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.GlobalSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal fun interface GlobalSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<GlobalSection, WasmDecodeError>
