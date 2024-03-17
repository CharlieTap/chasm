package io.github.charlietap.chasm.decoder.wasm.decoder.section.type

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import io.github.charlietap.chasm.decoder.wasm.section.TypeSection

internal fun interface TypeSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<TypeSection, WasmDecodeError>
