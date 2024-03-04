package io.github.charlietap.chasm.decoder.section.code

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.decoder.instruction.BinaryExpressionDecoder
import io.github.charlietap.chasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryCodeEntryDecoder(
    reader: WasmBinaryReader,
): Result<CodeEntry, WasmDecodeError> =
    BinaryCodeEntryDecoder(
        reader = reader,
        localDecoder = ::BinaryLocalDecoder,
        expressionDecoder = ::BinaryExpressionDecoder,
        vectorDecoder = ::BinaryVectorDecoder,
    )

fun BinaryCodeEntryDecoder(
    reader: WasmBinaryReader,
    localDecoder: LocalDecoder,
    expressionDecoder: ExpressionDecoder,
    vectorDecoder: VectorDecoder<Local>,
): Result<CodeEntry, WasmDecodeError> = binding {

    val size = reader.uint().bind()

    val locals = vectorDecoder(reader, localDecoder).bind()
    val expression = expressionDecoder(reader).bind()

    CodeEntry(size, locals.vector, expression)
}
