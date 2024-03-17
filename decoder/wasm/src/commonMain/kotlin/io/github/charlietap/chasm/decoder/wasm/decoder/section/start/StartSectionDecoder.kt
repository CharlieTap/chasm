package io.github.charlietap.chasm.decoder.wasm.decoder.section.start

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import io.github.charlietap.chasm.decoder.wasm.section.StartSection

internal fun interface StartSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<StartSection, WasmDecodeError>
