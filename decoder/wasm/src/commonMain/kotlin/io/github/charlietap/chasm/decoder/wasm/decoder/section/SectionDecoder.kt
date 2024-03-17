package io.github.charlietap.chasm.decoder.wasm.decoder.section

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.Section
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import io.github.charlietap.chasm.decoder.wasm.section.SectionType

internal fun interface SectionDecoder : (WasmBinaryReader, SectionType, SectionSize) -> Result<Section, WasmDecodeError>
