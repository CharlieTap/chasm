package io.github.charlietap.chasm.decoder.decoder.section.code

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun CodeEntryDecoder(
    context: DecoderContext,
): Result<CodeEntry, WasmDecodeError> =
    CodeEntryDecoder(
        context = context,
        localEntryDecoder = ::LocalEntryDecoder,
        expressionDecoder = ::ExpressionDecoder,
        vectorDecoder = ::VectorDecoder,
    )

internal inline fun CodeEntryDecoder(
    context: DecoderContext,
    noinline localEntryDecoder: Decoder<LocalEntry>,
    crossinline expressionDecoder: Decoder<Expression>,
    crossinline vectorDecoder: VectorDecoder<LocalEntry>,
): Result<CodeEntry, WasmDecodeError> = binding {

    val size = context.reader.uint().bind()
    val localEntries = vectorDecoder(context, localEntryDecoder).bind()
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

    val expression = expressionDecoder(context).bind()

    CodeEntry(size, locals, expression)
}
