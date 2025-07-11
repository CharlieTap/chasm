package io.github.charlietap.chasm.decoder.context

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader

internal data class DecoderContext(
    val config: ModuleConfig,
    val reader: WasmBinaryReader,
    val blockContext: BlockContext = BlockContextImpl(),
    val moduleContext: ModuleContext = ModuleContextImpl(),
    val nameSectionContext: NameSectionContext = NameSectionContextImpl(),
    val sectionContext: SectionContext = SectionContextImpl(),
    val typeContext: TypeContext = TypeContextImpl(),
    val vectorContext: VectorContext = VectorContextImpl(),
) : BlockContext by blockContext,
    ModuleContext by moduleContext,
    SectionContext by sectionContext,
    TypeContext by typeContext,
    VectorContext by vectorContext
