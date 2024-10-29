package io.github.charlietap.chasm.decoder.decoder.section.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.TableSection

internal fun TableSectionDecoder(
    context: DecoderContext,
): Result<TableSection, WasmDecodeError> =
    TableSectionDecoder(
        context = context,
        vectorDecoder = ::VectorDecoder,
        tableDecoder = ::TableDecoder,
    )

internal fun TableSectionDecoder(
    context: DecoderContext,
    vectorDecoder: VectorDecoder<Table>,
    tableDecoder: Decoder<Table>,
): Result<TableSection, WasmDecodeError> = binding {

    val tables = vectorDecoder(context, tableDecoder).bind()

    TableSection(tables.vector)
}
