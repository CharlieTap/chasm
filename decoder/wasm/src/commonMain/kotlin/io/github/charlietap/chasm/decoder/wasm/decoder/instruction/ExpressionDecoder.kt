package io.github.charlietap.chasm.decoder.wasm.decoder.instruction

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias ExpressionDecoder = (WasmBinaryReader) -> Result<Expression, WasmDecodeError>
