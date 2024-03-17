package io.github.charlietap.chasm.decoder.wasm.decoder.section.data

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.DataSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal fun interface DataSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<DataSection, WasmDecodeError>
