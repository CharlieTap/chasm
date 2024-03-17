package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BLOCK
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BR
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BR_IF
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BR_ON_NON_NULL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BR_ON_NULL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BR_TABLE
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BinaryInstructionBlockDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.CALL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.CALL_INDIRECT
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.CALL_REF
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.END
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.IF
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.InstructionBlockDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.LOOP
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.NOP
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.RETURN
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.RETURN_CALL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.RETURN_CALL_INDIRECT
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.RETURN_CALL_REF
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.UNREACHABLE
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryFunctionIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryLabelIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryTableIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryTypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.LabelIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryControlInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
): Result<Instruction, WasmDecodeError> =
    BinaryControlInstructionDecoder(
        reader = reader,
        opcode = opcode,
        blockTypeDecoder = ::BinaryBlockTypeDecoder,
        instructionBlockDecoder = ::BinaryInstructionBlockDecoder,
        ifDecoder = ::BinaryIfDecoder,
        functionIndexDecoder = ::BinaryFunctionIndexDecoder,
        typeIndexDecoder = ::BinaryTypeIndexDecoder,
        tableIndexDecoder = ::BinaryTableIndexDecoder,
        labelIndexDecoder = ::BinaryLabelIndexDecoder,
        vectorDecoder = ::BinaryVectorDecoder,
    )

internal fun BinaryControlInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
    blockTypeDecoder: BlockTypeDecoder,
    instructionBlockDecoder: InstructionBlockDecoder,
    ifDecoder: IfDecoder,
    functionIndexDecoder: FunctionIndexDecoder,
    typeIndexDecoder: TypeIndexDecoder,
    tableIndexDecoder: TableIndexDecoder,
    labelIndexDecoder: LabelIndexDecoder,
    vectorDecoder: VectorDecoder<Index.LabelIndex>,
): Result<Instruction, WasmDecodeError> = binding {
    when (opcode) {
        UNREACHABLE -> ControlInstruction.Unreachable
        NOP -> ControlInstruction.Nop
        BLOCK -> {
            val blockType = blockTypeDecoder(reader).bind()
            val instructions = instructionBlockDecoder(reader, END).bind()
            ControlInstruction.Block(blockType, instructions)
        }
        LOOP -> {
            val blockType = blockTypeDecoder(reader).bind()
            val instructions = instructionBlockDecoder(reader, END).bind()
            ControlInstruction.Loop(blockType, instructions)
        }
        IF -> {
            val blockType = blockTypeDecoder(reader).bind()
            val (thenInstructions, elseInstructions) = ifDecoder(reader).bind()
            ControlInstruction.If(blockType, thenInstructions, elseInstructions)
        }
        BR -> {
            val idx = labelIndexDecoder(reader).bind()
            ControlInstruction.Br(idx)
        }
        BR_IF -> {
            val idx = labelIndexDecoder(reader).bind()
            ControlInstruction.BrIf(idx)
        }
        BR_TABLE -> {
            val indices = vectorDecoder(reader, labelIndexDecoder).bind()
            val default = labelIndexDecoder(reader).bind()
            ControlInstruction.BrTable(indices.vector, default)
        }
        RETURN -> ControlInstruction.Return
        CALL -> {
            val idx = functionIndexDecoder(reader).bind()
            ControlInstruction.Call(idx)
        }
        CALL_INDIRECT -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            val tableIndex = tableIndexDecoder(reader).bind()
            ControlInstruction.CallIndirect(typeIndex, tableIndex)
        }
        RETURN_CALL -> {
            val idx = functionIndexDecoder(reader).bind()
            ControlInstruction.ReturnCall(idx)
        }
        RETURN_CALL_INDIRECT -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            val tableIndex = tableIndexDecoder(reader).bind()
            ControlInstruction.ReturnCallIndirect(typeIndex, tableIndex)
        }
        CALL_REF -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            ControlInstruction.CallRef(typeIndex)
        }
        RETURN_CALL_REF -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            ControlInstruction.ReturnCallRef(typeIndex)
        }
        BR_ON_NULL -> {
            val labelIndex = labelIndexDecoder(reader).bind()
            ControlInstruction.BrOnNull(labelIndex)
        }
        BR_ON_NON_NULL -> {
            val labelIndex = labelIndexDecoder(reader).bind()
            ControlInstruction.BrOnNonNull(labelIndex)
        }

        else -> Err(InstructionDecodeError.InvalidControlInstruction(opcode)).bind<Instruction>()
    }
}
