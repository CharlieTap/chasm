package io.github.charlietap.chasm.decoder.section.code

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Index
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
        localEntryDecoder = ::BinaryLocalEntryDecoder,
        expressionDecoder = ::BinaryExpressionDecoder,
        vectorDecoder = ::BinaryVectorDecoder,
    )

fun BinaryCodeEntryDecoder(
    reader: WasmBinaryReader,
    localEntryDecoder: LocalEntryDecoder,
    expressionDecoder: ExpressionDecoder,
    vectorDecoder: VectorDecoder<LocalEntry>,
): Result<CodeEntry, WasmDecodeError> = binding {

    val size = reader.uint().bind()

    val localEntries = vectorDecoder(reader, localEntryDecoder).bind()
    var index = 0u
    val locals = mutableListOf<Local>()
    localEntries.vector.forEach { entry ->
        repeat(entry.count.toInt()) {
            locals.add(Local(Index.LocalIndex(index), entry.type))
            index++
        }
    }

    val expression = expressionDecoder(reader).bind()

    CodeEntry(size, locals, expression)
}
