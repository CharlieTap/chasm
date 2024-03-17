package io.github.charlietap.chasm.decoder.wasm.decoder.value.name

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias NameValueDecoder = (WasmBinaryReader) -> Result<NameValue, WasmDecodeError>
