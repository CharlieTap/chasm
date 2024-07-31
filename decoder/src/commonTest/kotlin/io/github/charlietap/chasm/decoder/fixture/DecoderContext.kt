package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.decoder.context.BlockContextImpl
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.context.SectionContextImpl
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.decoder.section.SectionType

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
