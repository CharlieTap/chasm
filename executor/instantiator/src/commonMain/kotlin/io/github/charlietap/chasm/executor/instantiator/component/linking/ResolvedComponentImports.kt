package io.github.charlietap.chasm.executor.instantiator.component.linking

import io.github.charlietap.chasm.executor.instantiator.CompiledModule
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction

class ResolvedComponentImports internal constructor(
    internal val coreModules: Array<ResolvedCoreModuleImport>,
    internal val functions: Array<RuntimeComponentHostFunction>,
    internal val resourceTypes: IntArray,
)

internal data class ResolvedCoreModuleImport(
    val module: CompiledModule,
    val importIndexes: IntArray,
    val exportIndexes: IntArray,
)
