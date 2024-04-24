package io.github.charlietap.chasm.decoder.wasm.decoder.section.global

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.decoder.wasm.decoder.Decoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BinaryExpressionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.global.BinaryGlobalTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryGlobalDecoder(
    reader: WasmBinaryReader,
    index: Index.GlobalIndex,
): Result<Global, WasmDecodeError> = BinaryGlobalDecoder(
    reader = reader,
    index = index,
    globalTypeDecoder = ::BinaryGlobalTypeDecoder,
    expressionDecoder = ::BinaryExpressionDecoder,
)

internal fun BinaryGlobalDecoder(
    reader: WasmBinaryReader,
    index: Index.GlobalIndex,
    globalTypeDecoder: Decoder<GlobalType>,
    expressionDecoder: ExpressionDecoder,
): Result<Global, WasmDecodeError> = binding {
    val type = globalTypeDecoder(reader).bind()
    val expression = expressionDecoder(reader).bind()
    Global(index, type, expression)
}
