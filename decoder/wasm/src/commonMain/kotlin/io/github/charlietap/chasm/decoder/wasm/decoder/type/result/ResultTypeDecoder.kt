package io.github.charlietap.chasm.decoder.wasm.decoder.type.result

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias ResultTypeDecoder = (WasmBinaryReader) -> Result<ResultType, WasmDecodeError>
