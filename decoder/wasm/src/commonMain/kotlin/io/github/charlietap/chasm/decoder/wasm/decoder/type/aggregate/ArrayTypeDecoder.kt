package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias ArrayTypeDecoder = (WasmBinaryReader) -> Result<CompositeType.Array, WasmDecodeError>
