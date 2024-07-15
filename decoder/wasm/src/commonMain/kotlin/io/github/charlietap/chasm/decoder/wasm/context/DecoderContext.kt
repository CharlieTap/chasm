package io.github.charlietap.chasm.decoder.wasm.context

import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal data class DecoderContext(
    val reader: WasmBinaryReader,
    val sectionContext: SectionContext = SectionContextImpl(),
    val blockContext: BlockContext = BlockContextImpl(),
) : SectionContext by sectionContext, BlockContext by blockContext
