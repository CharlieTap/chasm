package io.github.charlietap.chasm.decoder.wasm.decoder.type.global

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias GlobalTypeDecoder = (WasmBinaryReader) -> Result<GlobalType, WasmDecodeError>
