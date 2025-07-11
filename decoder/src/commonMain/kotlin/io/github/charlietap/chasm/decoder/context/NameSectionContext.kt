package io.github.charlietap.chasm.decoder.context

internal interface NameSectionContext {
    val sectionSize: UInt
}

internal data class NameSectionContextImpl(
    override val sectionSize: UInt = 0u,
) : NameSectionContext
