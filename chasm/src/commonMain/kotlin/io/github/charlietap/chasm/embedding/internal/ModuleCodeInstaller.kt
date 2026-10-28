package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.ModuleCompiler
import io.github.charlietap.chasm.executor.instantiator.ModuleCodeInstaller

internal fun ModuleCodeInstaller(
    module: Module,
): ModuleCodeInstaller = { store, instance, runtimeTypes, types ->
    ModuleCompiler(store, module, instance, runtimeTypes, types)
}
