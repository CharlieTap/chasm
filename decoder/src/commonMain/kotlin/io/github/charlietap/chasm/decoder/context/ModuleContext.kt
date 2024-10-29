package io.github.charlietap.chasm.decoder.context

import io.github.charlietap.chasm.ast.module.Import

internal interface ModuleContext {
    var imports: List<Import>
}

internal data class ModuleContextImpl(
    override var imports: List<Import> = mutableListOf(),
) : ModuleContext
