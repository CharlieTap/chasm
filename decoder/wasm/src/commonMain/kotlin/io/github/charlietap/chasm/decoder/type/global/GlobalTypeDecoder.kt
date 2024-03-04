package io.github.charlietap.chasm.decoder.type.global

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias GlobalTypeDecoder = (WasmBinaryReader) -> Result<GlobalType, WasmDecodeError>
