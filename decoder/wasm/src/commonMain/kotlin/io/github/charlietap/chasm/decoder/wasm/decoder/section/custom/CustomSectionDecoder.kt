package io.github.charlietap.chasm.decoder.wasm.decoder.section.custom

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.CustomSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal fun interface CustomSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<CustomSection, WasmDecodeError>
