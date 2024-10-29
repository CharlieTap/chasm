package io.github.charlietap.chasm.decoder.decoder.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.index.MemoryIndexDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun MemArgWithIndexDecoder(
    context: DecoderContext,
): Result<MemArgWithIndex, WasmDecodeError> =
    MemArgWithIndexDecoder(
        context = context,
        memoryIndexDecoder = ::MemoryIndexDecoder,
        exponentValidator = ::AlignmentExponentValidator,
    )

internal inline fun MemArgWithIndexDecoder(
    context: DecoderContext,
    crossinline memoryIndexDecoder: Decoder<Index.MemoryIndex>,
    crossinline exponentValidator: AlignmentExponentValidator,
): Result<MemArgWithIndex, WasmDecodeError> = binding {

    val exponent = context.reader.uint().bind()
    if (exponent < EXPONENT_MEMIDX_THRESHOLD_MIN) {

        exponentValidator(exponent).bind()

        val offset = context.reader.uint().bind()
        val memArg = MemArg(exponent, offset)

        MemArgWithIndex(Index.MemoryIndex(0u), memArg)
    } else if (exponent < EXPONENT_MEMIDX_THRESHOLD_MAX) {

        val computedExponent = exponent - EXPONENT_MEMIDX_THRESHOLD_MIN
        exponentValidator(computedExponent).bind()

        val index = memoryIndexDecoder(context).bind()
        val offset = context.reader.uint().bind()

        MemArgWithIndex(index, MemArg(computedExponent, offset))
    } else {
        Err(InstructionDecodeError.InvalidAlignment(exponent)).bind()
    }
}

private const val EXPONENT_MEMIDX_THRESHOLD_MIN: UInt = 64u
private const val EXPONENT_MEMIDX_THRESHOLD_MAX: UInt = 128u
