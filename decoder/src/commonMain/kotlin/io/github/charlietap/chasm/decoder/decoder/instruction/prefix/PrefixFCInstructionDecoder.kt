package io.github.charlietap.chasm.decoder.decoder.instruction.prefix

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.DATA_DROP
import io.github.charlietap.chasm.decoder.decoder.instruction.ELEM_DROP
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_TRUNC_SAT_F32_S
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_TRUNC_SAT_F32_U
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_TRUNC_SAT_F64_S
import io.github.charlietap.chasm.decoder.decoder.instruction.I32_TRUNC_SAT_F64_U
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_TRUNC_SAT_F32_S
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_TRUNC_SAT_F32_U
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_TRUNC_SAT_F64_S
import io.github.charlietap.chasm.decoder.decoder.instruction.I64_TRUNC_SAT_F64_U
import io.github.charlietap.chasm.decoder.decoder.instruction.MEMORY_COPY
import io.github.charlietap.chasm.decoder.decoder.instruction.MEMORY_FILL
import io.github.charlietap.chasm.decoder.decoder.instruction.MEMORY_INIT
import io.github.charlietap.chasm.decoder.decoder.instruction.PREFIX_FC
import io.github.charlietap.chasm.decoder.decoder.instruction.TABLE_COPY
import io.github.charlietap.chasm.decoder.decoder.instruction.TABLE_FILL
import io.github.charlietap.chasm.decoder.decoder.instruction.TABLE_GROW
import io.github.charlietap.chasm.decoder.decoder.instruction.TABLE_INIT
import io.github.charlietap.chasm.decoder.decoder.instruction.TABLE_SIZE
import io.github.charlietap.chasm.decoder.decoder.section.index.DataIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.ElementIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.MemoryIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun PrefixFCInstructionDecoder(
    context: DecoderContext,
): Result<Instruction, WasmDecodeError> =
    PrefixFCInstructionDecoder(
        context = context,
        dataIndexDecoder = ::DataIndexDecoder,
        elementIndexDecoder = ::ElementIndexDecoder,
        memoryIndexDecoder = ::MemoryIndexDecoder,
        tableIndexDecoder = ::TableIndexDecoder,
    )

internal fun PrefixFCInstructionDecoder(
    context: DecoderContext,
    dataIndexDecoder: Decoder<Index.DataIndex>,
    elementIndexDecoder: Decoder<Index.ElementIndex>,
    memoryIndexDecoder: Decoder<Index.MemoryIndex>,
    tableIndexDecoder: Decoder<Index.TableIndex>,
): Result<Instruction, WasmDecodeError> = binding {

    when (val opcode = context.reader.uint().bind()) {

        I32_TRUNC_SAT_F32_S -> NumericInstruction.I32TruncSatF32S
        I32_TRUNC_SAT_F32_U -> NumericInstruction.I32TruncSatF32U
        I32_TRUNC_SAT_F64_S -> NumericInstruction.I32TruncSatF64S
        I32_TRUNC_SAT_F64_U -> NumericInstruction.I32TruncSatF64U
        I64_TRUNC_SAT_F32_S -> NumericInstruction.I64TruncSatF32S
        I64_TRUNC_SAT_F32_U -> NumericInstruction.I64TruncSatF32U
        I64_TRUNC_SAT_F64_S -> NumericInstruction.I64TruncSatF64S
        I64_TRUNC_SAT_F64_U -> NumericInstruction.I64TruncSatF64U

        MEMORY_INIT -> {
            val dataIndex = dataIndexDecoder(context).bind()
            val memoryIndex = memoryIndexDecoder(context).bind()
            MemoryInstruction.MemoryInit(memoryIndex, dataIndex)
        }

        DATA_DROP -> {
            val dataIndex = dataIndexDecoder(context).bind()
            MemoryInstruction.DataDrop(dataIndex)
        }

        MEMORY_COPY -> {
            val dstMemoryIndex = memoryIndexDecoder(context).bind()
            val srcMemoryIndex = memoryIndexDecoder(context).bind()
            MemoryInstruction.MemoryCopy(srcMemoryIndex, dstMemoryIndex)
        }

        MEMORY_FILL -> {
            val memoryIndex = memoryIndexDecoder(context).bind()
            MemoryInstruction.MemoryFill(memoryIndex)
        }

        TABLE_INIT -> {
            val elemIdx = elementIndexDecoder(context).bind()
            val tableIdx = tableIndexDecoder(context).bind()
            TableInstruction.TableInit(elemIdx, tableIdx)
        }
        ELEM_DROP -> {
            val elemIdx = elementIndexDecoder(context).bind()
            TableInstruction.ElemDrop(elemIdx)
        }
        TABLE_COPY -> {
            val destTableIdx = tableIndexDecoder(context).bind()
            val srcTableIdx = tableIndexDecoder(context).bind()
            TableInstruction.TableCopy(srcTableIdx, destTableIdx)
        }
        TABLE_GROW -> {
            val tableIdx = tableIndexDecoder(context).bind()
            TableInstruction.TableGrow(tableIdx)
        }
        TABLE_SIZE -> {
            val tableIdx = tableIndexDecoder(context).bind()
            TableInstruction.TableSize(tableIdx)
        }
        TABLE_FILL -> {
            val tableIdx = tableIndexDecoder(context).bind()
            TableInstruction.TableFill(tableIdx)
        }

        else -> Err(InstructionDecodeError.InvalidPrefixInstruction(PREFIX_FC, opcode)).bind<Instruction>()
    }
}
