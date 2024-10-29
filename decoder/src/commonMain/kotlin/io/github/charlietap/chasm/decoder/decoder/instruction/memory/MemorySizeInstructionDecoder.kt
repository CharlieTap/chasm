package io.github.charlietap.chasm.decoder.decoder.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.index.MemoryIndexDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun MemorySizeInstructionDecoder(
    context: DecoderContext,
): Result<MemoryInstruction.MemorySize, WasmDecodeError> =
    MemorySizeInstructionDecoder(
        context = context,
        memoryIndexDecoder = ::MemoryIndexDecoder,
    )

internal inline fun MemorySizeInstructionDecoder(
    context: DecoderContext,
    crossinline memoryIndexDecoder: Decoder<Index.MemoryIndex>,
): Result<MemoryInstruction.MemorySize, WasmDecodeError> = binding {
    val index = memoryIndexDecoder(context).bind()
    MemoryInstruction.MemorySize(index)
}
