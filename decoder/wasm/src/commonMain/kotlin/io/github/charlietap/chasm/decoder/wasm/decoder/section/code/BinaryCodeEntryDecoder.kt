package io.github.charlietap.chasm.decoder.wasm.decoder.section.code

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BinaryExpressionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryCodeEntryDecoder(
    reader: WasmBinaryReader,
): Result<CodeEntry, WasmDecodeError> =
    BinaryCodeEntryDecoder(
        reader = reader,
        localEntryDecoder = ::BinaryLocalEntryDecoder,
        expressionDecoder = ::BinaryExpressionDecoder,
        vectorDecoder = ::BinaryVectorDecoder,
    )

internal fun BinaryCodeEntryDecoder(
    reader: WasmBinaryReader,
    localEntryDecoder: LocalEntryDecoder,
    expressionDecoder: ExpressionDecoder,
    vectorDecoder: VectorDecoder<LocalEntry>,
): Result<CodeEntry, WasmDecodeError> = binding {

    val size = reader.uint().bind()
    val localEntries = vectorDecoder(reader, localEntryDecoder).bind()
    var index = 0u

    if (localEntries.vector.sumOf { it.count.toULong() } > UInt.MAX_VALUE.toULong()) {
        Err(SectionDecodeError.TooManyLocals).bind<Unit>()
    }

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
