package io.github.charlietap.chasm.decoder.decoder.section.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.MemorySection

internal fun MemorySectionDecoder(
    context: DecoderContext,
): Result<MemorySection, WasmDecodeError> =
    MemorySectionDecoder(
        context = context,
        vectorDecoder = ::VectorDecoder,
        memoryDecoder = ::MemoryDecoder,
    )

internal inline fun MemorySectionDecoder(
    context: DecoderContext,
    crossinline vectorDecoder: VectorDecoder<Memory>,
    noinline memoryDecoder: Decoder<Memory>,
): Result<MemorySection, WasmDecodeError> = binding {

    val memories = vectorDecoder(context, memoryDecoder).bind()

    MemorySection(memories.vector)
}
