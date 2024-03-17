package io.github.charlietap.chasm.decoder.wasm.decoder.type.memory

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias MemoryTypeDecoder = (WasmBinaryReader) -> Result<MemoryType, WasmDecodeError>
