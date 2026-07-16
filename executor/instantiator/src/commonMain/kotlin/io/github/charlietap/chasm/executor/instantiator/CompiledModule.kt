package io.github.charlietap.chasm.executor.instantiator

import io.github.charlietap.chasm.executor.instantiator.component.linking.CoreModuleLinkShape
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.ast.module.Module as ASTModule

class CompiledModule internal constructor(
    internal val module: Module,
    val sourceModule: ASTModule? = null,
) {
    internal val componentLinkShape = CoreModuleLinkShape(module)
}
