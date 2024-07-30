package io.github.charlietap.chasm.decoder.decoder.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.index.MemoryIndexDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun MemArgWithIndexDecoder(
    context: DecoderContext,
): Result<MemArgWithIndex, WasmDecodeError> =
    MemArgWithIndexDecoder(
        context = context,
        memoryIndexDecoder = ::MemoryIndexDecoder,
    )

internal fun MemArgWithIndexDecoder(
    context: DecoderContext,
    memoryIndexDecoder: Decoder<Index.MemoryIndex>,
): Result<MemArgWithIndex, WasmDecodeError> = binding {

    val alignment = context.reader.uint().bind()
    if (alignment < ALIGNMENT_THRESHOLD) {

        val offset = context.reader.uint().bind()
        val memArg = MemArg(alignment, offset)

        MemArgWithIndex(Index.MemoryIndex(0u), memArg)
    } else {

        val computedAlignment = alignment - ALIGNMENT_THRESHOLD
        val index = memoryIndexDecoder(context).bind()
        val offset = context.reader.uint().bind()

        MemArgWithIndex(index, MemArg(computedAlignment, offset))
    }
}

private const val ALIGNMENT_THRESHOLD: UInt = 64u
