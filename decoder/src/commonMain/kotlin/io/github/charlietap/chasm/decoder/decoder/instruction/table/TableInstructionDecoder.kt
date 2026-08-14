package io.github.charlietap.chasm.decoder.decoder.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.CodeBodyDecoderContext
import io.github.charlietap.chasm.decoder.decoder.CodeBodyDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.TABLE_GET
import io.github.charlietap.chasm.decoder.decoder.instruction.TABLE_SET
import io.github.charlietap.chasm.decoder.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun TableInstructionDecoder(
    context: CodeBodyDecoderContext,
): Result<TableInstruction, WasmDecodeError> =
    TableInstructionDecoder(
        context = context,
        tableIndexDecoder = ::TableIndexDecoder,
    )

internal inline fun TableInstructionDecoder(
    context: CodeBodyDecoderContext,
    crossinline tableIndexDecoder: CodeBodyDecoder<Index.TableIndex>,
): Result<TableInstruction, WasmDecodeError> = binding {
    when (val opcode = context.reader.ubyte()) {
        TABLE_GET -> {
            TableInstruction.TableGet(tableIndexDecoder(context).bind())
        }
        TABLE_SET -> {
            TableInstruction.TableSet(tableIndexDecoder(context).bind())
        }

        else -> Err(InstructionDecodeError.InvalidTableInstruction(opcode)).bind<TableInstruction>()
    }
}
