package io.github.charlietap.chasm.decoder.wasm.decoder.section.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorLengthDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorLengthDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import io.github.charlietap.chasm.decoder.wasm.section.TableSection

internal class BinaryTableSectionDecoder(
    private val vectorLengthDecoder: VectorLengthDecoder = ::BinaryVectorLengthDecoder,
    private val tableDecoder: TableDecoder = ::BinaryTableDecoder,
) : TableSectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<TableSection, WasmDecodeError> = binding {

        val vectorLength = vectorLengthDecoder(reader).bind()

        val tables = (0u..<vectorLength.length).map { idx ->
            tableDecoder(Index.TableIndex(idx), reader).bind()
        }

        TableSection(tables)
    }
}
