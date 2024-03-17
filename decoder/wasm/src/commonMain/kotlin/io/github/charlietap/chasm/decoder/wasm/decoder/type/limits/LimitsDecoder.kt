package io.github.charlietap.chasm.decoder.wasm.decoder.type.limits

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias LimitsDecoder = (WasmBinaryReader) -> Result<Limits, WasmDecodeError>
