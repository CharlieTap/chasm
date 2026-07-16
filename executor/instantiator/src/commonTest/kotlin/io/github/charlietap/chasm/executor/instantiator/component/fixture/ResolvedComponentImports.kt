package io.github.charlietap.chasm.executor.instantiator.component.fixture

import io.github.charlietap.chasm.executor.instantiator.CompiledModule
import io.github.charlietap.chasm.executor.instantiator.component.linking.ResolvedComponentImports
import io.github.charlietap.chasm.executor.instantiator.component.linking.ResolvedCoreModuleImport
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction

internal fun resolvedComponentImports(
    coreModules: Array<ResolvedCoreModuleImport> = emptyArray(),
    functions: Array<RuntimeComponentHostFunction> = emptyArray(),
    resourceTypes: IntArray = intArrayOf(),
) = ResolvedComponentImports(coreModules, functions, resourceTypes)

internal fun resolvedCoreModuleImport(
    module: CompiledModule = compiledModule(),
    importIndexes: IntArray = intArrayOf(),
    exportIndexes: IntArray = intArrayOf(),
) = ResolvedCoreModuleImport(
    module = module,
    importIndexes = importIndexes,
    exportIndexes = exportIndexes,
)
