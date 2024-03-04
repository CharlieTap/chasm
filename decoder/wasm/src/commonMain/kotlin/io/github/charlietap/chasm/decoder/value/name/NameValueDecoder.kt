package io.github.charlietap.chasm.decoder.value.name

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias NameValueDecoder = (WasmBinaryReader) -> Result<NameValue, WasmDecodeError>
