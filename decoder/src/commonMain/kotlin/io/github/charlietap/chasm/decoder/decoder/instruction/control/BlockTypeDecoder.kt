package io.github.charlietap.chasm.decoder.decoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.CodeBodyDecoderContext
import io.github.charlietap.chasm.decoder.decoder.CodeBodyDecoder
import io.github.charlietap.chasm.decoder.decoder.type.value.NUMBER_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.REFERENCE_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.VECTOR_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.ValueType

internal fun BlockTypeDecoder(
    context: CodeBodyDecoderContext,
): Result<BlockType, WasmDecodeError> = BlockTypeDecoder(
    context = context,
    valueTypeDecoder = ::ValueTypeDecoder,
)

internal inline fun BlockTypeDecoder(
    context: CodeBodyDecoderContext,
    crossinline valueTypeDecoder: CodeBodyDecoder<ValueType>,
): Result<BlockType, WasmDecodeError> = binding {

    val firstByte = context.reader
        .peekUByte()

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
            val idx = context.reader.s33()
            BlockType.SignedTypeIndex(idx.toInt())
        }
    }
}

internal const val BLOCK_TYPE_EMPTY: UByte = 0x40u
