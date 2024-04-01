package io.github.charlietap.chasm.decoder.wasm.decoder.type

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias MutabilityDecoder = (WasmBinaryReader) -> Result<Mutability, WasmDecodeError>
