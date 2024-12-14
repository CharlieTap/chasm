package io.github.charlietap.chasm.decoder.decoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction.BlockType
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.value.NUMBER_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.REFERENCE_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.VECTOR_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun BlockTypeDecoder(
    context: DecoderContext,
): Result<BlockType, WasmDecodeError> = BlockTypeDecoder(
    context = context,
    valueTypeDecoder = ::ValueTypeDecoder,
)

internal inline fun BlockTypeDecoder(
    context: DecoderContext,
    crossinline valueTypeDecoder: Decoder<ValueType>,
): Result<BlockType, WasmDecodeError> = binding {

    val firstByte = context.reader
        .peek()
        .ubyte()
        .bind()

    when (firstByte) {
        BLOCK_TYPE_EMPTY -> {
            context.reader.byte() // consume it
            BlockType.Empty
        }
        in NUMBER_TYPE_RANGE,
        in VECTOR_TYPE_RANGE,
        in REFERENCE_TYPE_RANGE,
        -> {
            BlockType.ValType(valueTypeDecoder(context).bind())
        }

        else -> {
            val idx = context.reader.s33().bind()
            BlockType.SignedTypeIndex(Index.TypeIndex(idx))
        }
    }
}

internal const val BLOCK_TYPE_EMPTY: UByte = 0x40u
