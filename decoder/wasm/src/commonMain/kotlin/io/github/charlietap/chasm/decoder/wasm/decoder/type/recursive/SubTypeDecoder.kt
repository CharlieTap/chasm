package io.github.charlietap.chasm.decoder.wasm.decoder.type.recursive

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias SubTypeDecoder = (WasmBinaryReader) -> Result<SubType, WasmDecodeError>
