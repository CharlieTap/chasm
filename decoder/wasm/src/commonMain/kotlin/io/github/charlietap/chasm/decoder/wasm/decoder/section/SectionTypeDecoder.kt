package io.github.charlietap.chasm.decoder.wasm.decoder.section

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.SectionType

internal typealias SectionTypeDecoder = (WasmBinaryReader) -> Result<SectionType, WasmDecodeError>
