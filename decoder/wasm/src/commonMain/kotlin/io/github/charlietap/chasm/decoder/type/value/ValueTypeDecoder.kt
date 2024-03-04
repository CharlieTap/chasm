package io.github.charlietap.chasm.decoder.type.value

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias ValueTypeDecoder = (WasmBinaryReader) -> Result<ValueType, WasmDecodeError>
