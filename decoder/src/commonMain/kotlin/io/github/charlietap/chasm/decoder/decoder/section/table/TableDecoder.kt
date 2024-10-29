package io.github.charlietap.chasm.decoder.decoder.section.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.decoder.type.table.TableTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun TableDecoder(
    context: DecoderContext,
): Result<Table, WasmDecodeError> =
    TableDecoder(
        context = context,
        tableTypeDecoder = ::TableTypeDecoder,
        expressionDecoder = ::ExpressionDecoder,
    )

internal fun TableDecoder(
    context: DecoderContext,
    tableTypeDecoder: Decoder<TableType>,
    expressionDecoder: Decoder<Expression>,
): Result<Table, WasmDecodeError> = binding {

    val opcode = context.reader.peek().ubyte().bind()

    val tableImportCount = context.imports.count { it.descriptor is Import.Descriptor.Table }
    val index = tableImportCount + context.index
    val tableIndex = Index.TableIndex(index.toUInt())

    when (opcode) {
        OPCODE_TABLE_WITH_EXPRESSION -> {

            context.reader.ubyte().bind()
            context.reader.ubyte().bind()

            val tableType = tableTypeDecoder(context).bind()
            val initExpression = expressionDecoder(context).bind()
            Table(tableIndex, tableType, initExpression)
        }
        else -> {
            val tableType = tableTypeDecoder(context).bind()
            val heapType = tableType.referenceType.heapType

            val initExpression = Expression(listOf(ReferenceInstruction.RefNull(heapType)))
            Table(tableIndex, tableType, initExpression)
        }
    }
}

internal const val OPCODE_TABLE_WITH_EXPRESSION: UByte = 0x40u
