package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.moduleInfo
import io.github.charlietap.chasm.executor.instantiator.CompiledModule
import io.github.charlietap.chasm.ast.module.Module as InternalModule

class Module internal constructor(
    internal val config: ModuleConfig,
    internal val module: InternalModule,
    internal var compilationCache: ModuleCompilationCache<CompiledModule>? = null,
) : ComponentExportable {
    private val info by lazy {
        moduleInfo(this)
    }

    val imports by lazy {
        info.imports
    }

    val exports by lazy {
        info.exports
    }
}
