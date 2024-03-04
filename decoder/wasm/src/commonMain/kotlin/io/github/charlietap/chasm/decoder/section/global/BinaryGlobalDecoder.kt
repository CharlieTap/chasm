package io.github.charlietap.chasm.decoder.section.global

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.decoder.instruction.BinaryExpressionDecoder
import io.github.charlietap.chasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.type.global.BinaryGlobalTypeDecoder
import io.github.charlietap.chasm.decoder.type.global.GlobalTypeDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryGlobalDecoder(
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
    globalTypeDecoder: GlobalTypeDecoder,
    expressionDecoder: ExpressionDecoder,
): Result<Global, WasmDecodeError> = binding {
    val type = globalTypeDecoder(reader).bind()
    val expression = expressionDecoder(reader).bind()
    Global(index, type, expression)
}
