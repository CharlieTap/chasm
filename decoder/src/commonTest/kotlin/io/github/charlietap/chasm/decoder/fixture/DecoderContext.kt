package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.fixture.config.moduleConfig
import io.github.charlietap.chasm.decoder.context.BlockContextImpl
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.context.ModuleContextImpl
import io.github.charlietap.chasm.decoder.context.SectionContextImpl
import io.github.charlietap.chasm.decoder.context.TypeContextImpl
import io.github.charlietap.chasm.decoder.context.VectorContextImpl
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.decoder.section.SectionType
import io.github.charlietap.chasm.type.DefinedType

internal fun decoderContext(
    reader: WasmBinaryReader = FakeWasmBinaryReader(),
    config: ModuleConfig = moduleConfig(),
    sectionSize: SectionSize = sectionSize(),
    sectionType: SectionType = sectionType(),
    types: MutableList<Type> = mutableListOf(),
    definedTypes: MutableList<DefinedType> = mutableListOf(),
    blockEndOpcode: UByte = 0u,
    imports: List<Import> = emptyList(),
    index: Int = 0,
) = DecoderContext(
    config = config,
    reader = reader,
    blockContext = BlockContextImpl(
        blockEndOpcode = blockEndOpcode,
    ),
    moduleContext = ModuleContextImpl(
        imports = imports,
    ),
    sectionContext = SectionContextImpl(
        sectionSize = sectionSize,
        sectionType = sectionType,
    ),
    typeContext = TypeContextImpl(
        types = types,
        definedTypes = definedTypes,
    ),
    vectorContext = VectorContextImpl(
        index = index,
    ),
)
