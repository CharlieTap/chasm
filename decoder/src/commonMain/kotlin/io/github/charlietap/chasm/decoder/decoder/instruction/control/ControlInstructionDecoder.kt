package io.github.charlietap.chasm.decoder.decoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Index.TagIndex
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.context.scope.BlockScope
import io.github.charlietap.chasm.decoder.context.scope.Scope
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.BLOCK
import io.github.charlietap.chasm.decoder.decoder.instruction.BR
import io.github.charlietap.chasm.decoder.decoder.instruction.BR_IF
import io.github.charlietap.chasm.decoder.decoder.instruction.BR_ON_NON_NULL
import io.github.charlietap.chasm.decoder.decoder.instruction.BR_ON_NULL
import io.github.charlietap.chasm.decoder.decoder.instruction.BR_TABLE
import io.github.charlietap.chasm.decoder.decoder.instruction.CALL
import io.github.charlietap.chasm.decoder.decoder.instruction.CALL_INDIRECT
import io.github.charlietap.chasm.decoder.decoder.instruction.CALL_REF
import io.github.charlietap.chasm.decoder.decoder.instruction.END
import io.github.charlietap.chasm.decoder.decoder.instruction.IF
import io.github.charlietap.chasm.decoder.decoder.instruction.InstructionBlockDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.LOOP
import io.github.charlietap.chasm.decoder.decoder.instruction.NOP
import io.github.charlietap.chasm.decoder.decoder.instruction.RETURN
import io.github.charlietap.chasm.decoder.decoder.instruction.RETURN_CALL
import io.github.charlietap.chasm.decoder.decoder.instruction.RETURN_CALL_INDIRECT
import io.github.charlietap.chasm.decoder.decoder.instruction.RETURN_CALL_REF
import io.github.charlietap.chasm.decoder.decoder.instruction.THROW
import io.github.charlietap.chasm.decoder.decoder.instruction.THROW_REF
import io.github.charlietap.chasm.decoder.decoder.instruction.TRY_TABLE
import io.github.charlietap.chasm.decoder.decoder.instruction.UNREACHABLE
import io.github.charlietap.chasm.decoder.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.LabelIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.TagIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

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
        handlerDecoder = ::CatchHandlerDecoder,
        tagIndexDecoder = ::TagIndexDecoder,
        typeIndexDecoder = ::TypeIndexDecoder,
        tableIndexDecoder = ::TableIndexDecoder,
        labelIndexDecoder = ::LabelIndexDecoder,
        handlerVectorDecoder = ::VectorDecoder,
        labelVectorDecoder = ::VectorDecoder,
    )

internal fun ControlInstructionDecoder(
    context: DecoderContext,
    scope: Scope<UByte>,
    blockTypeDecoder: Decoder<ControlInstruction.BlockType>,
    instructionBlockDecoder: Decoder<List<Instruction>>,
    ifDecoder: Decoder<Pair<List<Instruction>, List<Instruction>?>>,
    functionIndexDecoder: Decoder<Index.FunctionIndex>,
    handlerDecoder: Decoder<ControlInstruction.CatchHandler>,
    tagIndexDecoder: Decoder<TagIndex>,
    typeIndexDecoder: Decoder<Index.TypeIndex>,
    tableIndexDecoder: Decoder<Index.TableIndex>,
    labelIndexDecoder: Decoder<Index.LabelIndex>,
    handlerVectorDecoder: VectorDecoder<ControlInstruction.CatchHandler>,
    labelVectorDecoder: VectorDecoder<Index.LabelIndex>,
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
        THROW -> {
            val tagIndex = tagIndexDecoder(context).bind()
            ControlInstruction.Throw(tagIndex)
        }
        THROW_REF -> ControlInstruction.ThrowRef
        BR -> {
            val idx = labelIndexDecoder(context).bind()
            ControlInstruction.Br(idx)
        }
        BR_IF -> {
            val idx = labelIndexDecoder(context).bind()
            ControlInstruction.BrIf(idx)
        }
        BR_TABLE -> {
            val indices = labelVectorDecoder(context, labelIndexDecoder).bind()
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
        TRY_TABLE -> {
            val blockType = blockTypeDecoder(context).bind()
            val handlers = handlerVectorDecoder(context, handlerDecoder).bind()
            val scopedContext = scope(context, END).bind()
            val instructions = instructionBlockDecoder(scopedContext).bind()

            ControlInstruction.TryTable(blockType, handlers.vector, instructions)
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
