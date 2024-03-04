package io.github.charlietap.chasm.decoder.type.limits

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias LimitsDecoder = (WasmBinaryReader) -> Result<Limits, WasmDecodeError>
