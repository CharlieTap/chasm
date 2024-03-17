package io.github.charlietap.chasm.decoder.wasm.decoder.type.function

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias FunctionTypeDecoder = (WasmBinaryReader) -> Result<FunctionType, WasmDecodeError>
