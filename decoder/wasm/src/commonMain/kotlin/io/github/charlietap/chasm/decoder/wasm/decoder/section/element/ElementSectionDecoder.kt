package io.github.charlietap.chasm.decoder.wasm.decoder.section.element

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.ElementSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal fun interface ElementSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<ElementSection, WasmDecodeError>
