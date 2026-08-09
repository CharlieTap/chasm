package io.github.charlietap.chasm.decoder.decoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Index.TagIndex
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
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
import io.github.charlietap.chasm.decoder.decoder.instruction.IF
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
import io.github.charlietap.chasm.type.BlockType

internal fun ControlInstructionDecoder(
    context: ModuleDecoderContext,
): Result<ControlInstruction, WasmDecodeError> =
    ControlInstructionDecoder(
        context = context,
        blockTypeDecoder = ::BlockTypeDecoder,
        functionIndexDecoder = ::FunctionIndexDecoder,
        handlerDecoder = ::CatchHandlerDecoder,
        tagIndexDecoder = ::TagIndexDecoder,
        typeIndexDecoder = ::TypeIndexDecoder,
        tableIndexDecoder = ::TableIndexDecoder,
        labelIndexDecoder = ::LabelIndexDecoder,
        handlerVectorDecoder = ::VectorDecoder,
        labelVectorDecoder = ::VectorDecoder,
    )

internal inline fun ControlInstructionDecoder(
    context: ModuleDecoderContext,
    crossinline blockTypeDecoder: Decoder<BlockType>,
    crossinline functionIndexDecoder: Decoder<Index.FunctionIndex>,
    noinline handlerDecoder: Decoder<ControlInstruction.CatchHandler>,
    crossinline tagIndexDecoder: Decoder<TagIndex>,
    crossinline typeIndexDecoder: Decoder<Index.TypeIndex>,
    crossinline tableIndexDecoder: Decoder<Index.TableIndex>,
    noinline labelIndexDecoder: Decoder<Index.LabelIndex>,
    crossinline handlerVectorDecoder: VectorDecoder<ControlInstruction.CatchHandler>,
    crossinline labelVectorDecoder: VectorDecoder<Index.LabelIndex>,
): Result<ControlInstruction, WasmDecodeError> = binding {
    when (val opcode = context.reader.ubyte()) {
        UNREACHABLE -> ControlInstruction.Unreachable
        NOP -> ControlInstruction.Nop
        BLOCK -> {
            val blockType = blockTypeDecoder(context).bind()
            ControlInstruction.Block(blockType)
        }
        LOOP -> {
            val blockType = blockTypeDecoder(context).bind()
            ControlInstruction.Loop(blockType)
        }
        IF -> {
            val blockType = blockTypeDecoder(context).bind()
            ControlInstruction.If(blockType)
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
            ControlInstruction.TryTable(blockType, handlers.vector)
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
