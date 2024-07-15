package io.github.charlietap.chasm.decoder.wasm.fixture

import io.github.charlietap.chasm.decoder.wasm.context.BlockContextImpl
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.context.SectionContextImpl
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import io.github.charlietap.chasm.decoder.wasm.section.SectionType

internal fun decoderContext(
    reader: WasmBinaryReader = FakeWasmBinaryReader(),
    sectionSize: SectionSize = sectionSize(),
    sectionType: SectionType = sectionType(),
    blockEndOpcode: UByte = 0u,
) = DecoderContext(
    reader = reader,
    sectionContext = SectionContextImpl(
        sectionSize = sectionSize,
        sectionType = sectionType,
    ),
    blockContext = BlockContextImpl(
        blockEndOpcode = blockEndOpcode,
    ),
)
