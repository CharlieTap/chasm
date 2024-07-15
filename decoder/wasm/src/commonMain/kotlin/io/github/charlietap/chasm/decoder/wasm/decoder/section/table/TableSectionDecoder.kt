package io.github.charlietap.chasm.decoder.wasm.decoder.section.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorLengthDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorLengthDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.section.TableSection

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
