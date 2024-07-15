package io.github.charlietap.chasm.decoder.wasm.context

import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import io.github.charlietap.chasm.decoder.wasm.section.SectionType

internal interface SectionContext {
    val sectionSize: SectionSize
    val sectionType: SectionType
}

internal data class SectionContextImpl(
    override val sectionSize: SectionSize = SectionSize(0u),
    override val sectionType: SectionType = SectionType.Custom,
) : SectionContext
