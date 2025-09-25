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

internal inline fun PrefixFCInstructionDecoder(
    context: DecoderContext,
    crossinline dataIndexDecoder: Decoder<Index.DataIndex>,
    crossinline elementIndexDecoder: Decoder<Index.ElementIndex>,
    crossinline memoryIndexDecoder: Decoder<Index.MemoryIndex>,
    crossinline tableIndexDecoder: Decoder<Index.TableIndex>,
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
        I64_ADD_128 -> NumericInstruction.I64Add128
        I64_SUB_128 -> NumericInstruction.I64Sub128
        I64_MUL_WIDE_S -> NumericInstruction.I64MulWideS
        I64_MUL_WIDE_U -> NumericInstruction.I64MulWideU

        else -> Err(InstructionDecodeError.InvalidPrefixInstruction(PREFIX_FC, opcode)).bind<Instruction>()
    }
}

internal const val I32_TRUNC_SAT_F32_S: UInt = 0u
internal const val I32_TRUNC_SAT_F32_U: UInt = 1u
internal const val I32_TRUNC_SAT_F64_S: UInt = 2u
internal const val I32_TRUNC_SAT_F64_U: UInt = 3u
internal const val I64_TRUNC_SAT_F32_S: UInt = 4u
internal const val I64_TRUNC_SAT_F32_U: UInt = 5u
internal const val I64_TRUNC_SAT_F64_S: UInt = 6u
internal const val I64_TRUNC_SAT_F64_U: UInt = 7u

internal const val MEMORY_INIT: UInt = 8u
internal const val DATA_DROP: UInt = 9u
internal const val MEMORY_COPY: UInt = 10u
internal const val MEMORY_FILL: UInt = 11u

internal const val TABLE_INIT: UInt = 12u
internal const val ELEM_DROP: UInt = 13u
internal const val TABLE_COPY: UInt = 14u
internal const val TABLE_GROW: UInt = 15u
internal const val TABLE_SIZE: UInt = 16u
internal const val TABLE_FILL: UInt = 17u

internal const val I64_ADD_128: UInt = 19u
internal const val I64_SUB_128: UInt = 20u
internal const val I64_MUL_WIDE_S: UInt = 21u
internal const val I64_MUL_WIDE_U: UInt = 22u
