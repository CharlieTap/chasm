package io.github.charlietap.chasm.decoder.context

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.decoder.section.SectionType
import io.github.charlietap.chasm.type.DefinedType

internal class ModuleDecoderContext(
    val config: ModuleConfig,
    override var reader: WasmBinaryReader,
    override var imports: List<Import> = emptyList(),
    override var requiresDataCount: Boolean = false,
    override var nameSectionSize: UInt = 0u,
    override var sectionSize: SectionSize = SectionSize(0u),
    override var sectionType: SectionType = SectionType.Custom,
    override val types: MutableList<Type> = mutableListOf(),
    override val definedTypes: MutableList<DefinedType> = mutableListOf(),
    override var index: Int = 0,
) : ReaderContext,
    ModuleContext,
    NameSectionContext,
    SectionContext,
    TypeContext,
    VectorContext {

    internal fun reset() {
        imports = emptyList()
        requiresDataCount = false
        nameSectionSize = 0u
        sectionSize = SectionSize(0u)
        sectionType = SectionType.Custom
        types.clear()
        definedTypes.clear()
        index = 0
    }
}
