package io.github.charlietap.chasm.decoder.instruction

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias ExpressionDecoder = (WasmBinaryReader) -> Result<Expression, WasmDecodeError>
