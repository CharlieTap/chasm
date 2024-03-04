package io.github.charlietap.chasm.decoder.type.result

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias ResultTypeDecoder = (WasmBinaryReader) -> Result<ResultType, WasmDecodeError>
