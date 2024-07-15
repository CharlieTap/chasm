package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.context.scope.BlockScope
import io.github.charlietap.chasm.decoder.wasm.context.scope.Scope
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BLOCK
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BR
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BR_IF
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BR_ON_NON_NULL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BR_ON_NULL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BR_TABLE
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
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.LabelIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun ControlInstructionDecoder(
    context: DecoderContext,
): Result<ControlInstruction, WasmDecodeError> =
    ControlInstructionDecoder(
        context = context,
        scope = ::BlockScope,
        blockTypeDecoder = ::BlockTypeDecoder,
        instructionBlockDecoder = ::InstructionBlockDecoder,
        ifDecoder = ::IfDecoder,
        functionIndexDecoder = ::FunctionIndexDecoder,
        typeIndexDecoder = ::TypeIndexDecoder,
        tableIndexDecoder = ::TableIndexDecoder,
        labelIndexDecoder = ::LabelIndexDecoder,
        vectorDecoder = ::VectorDecoder,
    )

internal fun ControlInstructionDecoder(
    context: DecoderContext,
    scope: Scope<UByte>,
    blockTypeDecoder: Decoder<ControlInstruction.BlockType>,
    instructionBlockDecoder: Decoder<List<Instruction>>,
    ifDecoder: Decoder<Pair<List<Instruction>, List<Instruction>?>>,
    functionIndexDecoder: Decoder<Index.FunctionIndex>,
    typeIndexDecoder: Decoder<Index.TypeIndex>,
    tableIndexDecoder: Decoder<Index.TableIndex>,
    labelIndexDecoder: Decoder<Index.LabelIndex>,
    vectorDecoder: VectorDecoder<Index.LabelIndex>,
): Result<ControlInstruction, WasmDecodeError> = binding {
    when (val opcode = context.reader.ubyte().bind()) {
        UNREACHABLE -> ControlInstruction.Unreachable
        NOP -> ControlInstruction.Nop
        BLOCK -> {
            val blockType = blockTypeDecoder(context).bind()
            val scopedContext = scope(context, END).bind()
            val instructions = instructionBlockDecoder(scopedContext).bind()
            ControlInstruction.Block(blockType, instructions)
        }
        LOOP -> {
            val blockType = blockTypeDecoder(context).bind()
            val scopedContext = scope(context, END).bind()
            val instructions = instructionBlockDecoder(scopedContext).bind()
            ControlInstruction.Loop(blockType, instructions)
        }
        IF -> {
            val blockType = blockTypeDecoder(context).bind()
            val (thenInstructions, elseInstructions) = ifDecoder(context).bind()
            ControlInstruction.If(blockType, thenInstructions, elseInstructions)
        }
        BR -> {
            val idx = labelIndexDecoder(context).bind()
            ControlInstruction.Br(idx)
        }
        BR_IF -> {
            val idx = labelIndexDecoder(context).bind()
            ControlInstruction.BrIf(idx)
        }
        BR_TABLE -> {
            val indices = vectorDecoder(context, labelIndexDecoder).bind()
            val default = labelIndexDecoder(context).bind()
            ControlInstruction.BrTable(indices.vector, default)
        }
        RETURN -> ControlInstruction.Return
        CALL -> {
            val idx = functionIndexDecoder(context).bind()
            ControlInstruction.Call(idx)
        }
        CALL_INDIRECT -> {
            val typeIndex = typeIndexDecoder(context).bind()
            val tableIndex = tableIndexDecoder(context).bind()
            ControlInstruction.CallIndirect(typeIndex, tableIndex)
        }
        RETURN_CALL -> {
            val idx = functionIndexDecoder(context).bind()
            ControlInstruction.ReturnCall(idx)
        }
        RETURN_CALL_INDIRECT -> {
            val typeIndex = typeIndexDecoder(context).bind()
            val tableIndex = tableIndexDecoder(context).bind()
            ControlInstruction.ReturnCallIndirect(typeIndex, tableIndex)
        }
        CALL_REF -> {
            val typeIndex = typeIndexDecoder(context).bind()
            ControlInstruction.CallRef(typeIndex)
        }
        RETURN_CALL_REF -> {
            val typeIndex = typeIndexDecoder(context).bind()
            ControlInstruction.ReturnCallRef(typeIndex)
        }
        BR_ON_NULL -> {
            val labelIndex = labelIndexDecoder(context).bind()
            ControlInstruction.BrOnNull(labelIndex)
        }
        BR_ON_NON_NULL -> {
            val labelIndex = labelIndexDecoder(context).bind()
            ControlInstruction.BrOnNonNull(labelIndex)
        }

        else -> Err(InstructionDecodeError.InvalidControlInstruction(opcode)).bind<ControlInstruction>()
    }
}
