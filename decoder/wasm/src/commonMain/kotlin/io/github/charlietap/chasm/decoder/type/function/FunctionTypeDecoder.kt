package io.github.charlietap.chasm.decoder.type.function

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias FunctionTypeDecoder = (WasmBinaryReader) -> Result<FunctionType, WasmDecodeError>
