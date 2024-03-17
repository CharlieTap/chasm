package io.github.charlietap.chasm.decoder.wasm.decoder.section.export

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.ExportSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal fun interface ExportSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<ExportSection, WasmDecodeError>
