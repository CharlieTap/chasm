package io.github.charlietap.chasm.decoder.decoder.section.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.BinaryVectorLengthDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorLengthDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.TableSection

internal fun TableSectionDecoder(
    context: DecoderContext,
): Result<TableSection, WasmDecodeError> =
    TableSectionDecoder(
        context = context,
        vectorLengthDecoder = ::BinaryVectorLengthDecoder,
        tableDecoder = ::TableDecoder,
    )

internal fun TableSectionDecoder(
    context: DecoderContext,
    vectorLengthDecoder: VectorLengthDecoder = ::BinaryVectorLengthDecoder,
    tableDecoder: Decoder<Table>,
): Result<TableSection, WasmDecodeError> = binding {

    val vectorLength = vectorLengthDecoder(context.reader).bind()

    val tables = (0u..<vectorLength.length).map { idx ->
        tableDecoder(context).bind().copy(
            idx = Index.TableIndex(idx),
        )
    }

    TableSection(tables)
}
