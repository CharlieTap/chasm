package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.decoder.section.SectionType
import io.github.charlietap.chasm.fixture.config.moduleConfig
import io.github.charlietap.chasm.type.DefinedType

internal fun decoderContext(
    reader: WasmBinaryReader = FakeWasmBinaryReader(),
    config: ModuleConfig = moduleConfig(),
    sectionSize: SectionSize = sectionSize(),
    sectionType: SectionType = sectionType(),
    types: MutableList<Type> = [],
    definedTypes: MutableList<DefinedType> = [],
    imports: List<Import> = [],
    index: Int = 0,
    nameSectionSize: UInt = 0u,
    requiresDataCount: Boolean = false,
) = ModuleDecoderContext(
    config = config,
    reader = reader,
    imports = imports,
    requiresDataCount = requiresDataCount,
    nameSectionSize = nameSectionSize,
    sectionSize = sectionSize,
    sectionType = sectionType,
    types = types,
    definedTypes = definedTypes,
    index = index,
)
