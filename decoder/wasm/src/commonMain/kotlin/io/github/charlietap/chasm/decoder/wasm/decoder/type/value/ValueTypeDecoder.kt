package io.github.charlietap.chasm.decoder.wasm.decoder.type.value

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias ValueTypeDecoder = (WasmBinaryReader) -> Result<ValueType, WasmDecodeError>
