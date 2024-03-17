package io.github.charlietap.chasm.decoder.wasm.decoder.type.heap

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias HeapTypeDecoder = (WasmBinaryReader) -> Result<HeapType, WasmDecodeError>
