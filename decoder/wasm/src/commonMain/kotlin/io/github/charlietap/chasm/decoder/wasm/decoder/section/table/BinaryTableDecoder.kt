package io.github.charlietap.chasm.decoder.wasm.decoder.section.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BinaryExpressionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.table.BinaryTableTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.table.TableTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryTableDecoder(
    index: Index.TableIndex,
    reader: WasmBinaryReader,
): Result<Table, WasmDecodeError> =
    BinaryTableDecoder(
        index = index,
        reader = reader,
        tableTypeDecoder = ::BinaryTableTypeDecoder,
        expressionDecoder = ::BinaryExpressionDecoder,
    )

internal fun BinaryTableDecoder(
    index: Index.TableIndex,
    reader: WasmBinaryReader,
    tableTypeDecoder: TableTypeDecoder,
    expressionDecoder: ExpressionDecoder,
): Result<Table, WasmDecodeError> = binding {

    val opcode = reader.peek().ubyte().bind()

    when (opcode) {
        OPCODE_TABLE_WITH_EXPRESSION -> {

            reader.ubyte().bind()
            reader.ubyte().bind()

            val tableType = tableTypeDecoder(reader).bind()
            val initExpression = expressionDecoder(reader).bind()
            Table(index, tableType, initExpression)
        }
        else -> {
            val tableType = tableTypeDecoder(reader).bind()
            val heapType = tableType.referenceType.heapType

            val initExpression = Expression(listOf(ReferenceInstruction.RefNull(heapType)))
            Table(index, tableType, initExpression)
        }
    }
}

internal const val OPCODE_TABLE_WITH_EXPRESSION: UByte = 0x40u
