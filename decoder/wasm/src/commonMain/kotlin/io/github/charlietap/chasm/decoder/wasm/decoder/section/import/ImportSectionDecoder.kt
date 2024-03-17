package io.github.charlietap.chasm.decoder.wasm.decoder.section.import

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.ImportSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal fun interface ImportSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<ImportSection, WasmDecodeError>
