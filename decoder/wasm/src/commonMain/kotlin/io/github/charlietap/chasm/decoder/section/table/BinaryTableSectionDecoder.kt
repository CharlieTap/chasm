package io.github.charlietap.chasm.decoder.section.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.decoder.type.table.BinaryTableTypeDecoder
import io.github.charlietap.chasm.decoder.type.table.TableTypeDecoder
import io.github.charlietap.chasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.SectionSize
import io.github.charlietap.chasm.section.TableSection

class BinaryTableSectionDecoder(
    private val vectorDecoder: VectorDecoder<TableType> = ::BinaryVectorDecoder,
    private val tableTypeDecoder: TableTypeDecoder = ::BinaryTableTypeDecoder,
) : TableSectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<TableSection, WasmDecodeError> = binding {

        val tablesTypes = vectorDecoder(reader, tableTypeDecoder).bind()

        val tables = tablesTypes.vector.mapIndexed { idx, tableType ->
            val index = Index.TableIndex(idx.toUInt())
            Table(index, tableType)
        }

        TableSection(tables)
    }
}
