package io.github.charlietap.chasm.decoder.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.decoder.instruction.TABLE_GET
import io.github.charlietap.chasm.decoder.instruction.TABLE_SET
import io.github.charlietap.chasm.decoder.section.index.BinaryTableIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.error.InstructionDecodeError
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryTableInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
): Result<Instruction, WasmDecodeError> =
    BinaryTableInstructionDecoder(
        reader = reader,
        opcode = opcode,
        tableIndexDecoder = ::BinaryTableIndexDecoder,
    )

fun BinaryTableInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
    tableIndexDecoder: TableIndexDecoder,
): Result<Instruction, WasmDecodeError> = binding {
    when (opcode) {
        TABLE_GET -> {
            TableInstruction.TableGet(tableIndexDecoder(reader).bind())
        }
        TABLE_SET -> {
            TableInstruction.TableSet(tableIndexDecoder(reader).bind())
        }

        else -> Err(InstructionDecodeError.InvalidTableInstruction(opcode)).bind<Instruction>()
    }
}
