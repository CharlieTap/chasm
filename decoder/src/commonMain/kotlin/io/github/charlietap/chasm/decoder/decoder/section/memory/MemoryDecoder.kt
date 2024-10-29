package io.github.charlietap.chasm.decoder.decoder.section.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.memory.MemoryTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun MemoryDecoder(
    context: DecoderContext,
): Result<Memory, WasmDecodeError> = MemoryDecoder(
    context = context,
    memoryTypeDecoder = ::MemoryTypeDecoder,
)

internal inline fun MemoryDecoder(
    context: DecoderContext,
    crossinline memoryTypeDecoder: Decoder<MemoryType>,
): Result<Memory, WasmDecodeError> = binding {

    val type = memoryTypeDecoder(context).bind()

    val memoryImportCount = context.imports.count { it.descriptor is Import.Descriptor.Memory }
    val index = memoryImportCount + context.index
    val memoryIndex = Index.MemoryIndex(index.toUInt())

    Memory(memoryIndex, type)
}
