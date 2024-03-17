package io.github.charlietap.chasm.decoder.wasm.decoder.section.datacount

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.DataCountSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal fun interface DataCountSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<DataCountSection, WasmDecodeError>
