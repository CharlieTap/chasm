package io.github.charlietap.chasm.decoder.wasm.decoder.section.code

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias CodeEntryDecoder = (WasmBinaryReader) -> Result<CodeEntry, WasmDecodeError>
