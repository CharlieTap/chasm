package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.backend.chasm.ModuleLinker
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.embedding.ext.toModuleTrapError
import io.github.charlietap.chasm.executor.instantiator.ModuleCodeInstaller

internal fun ModuleCodeInstaller(
    module: Module,
): ModuleCodeInstaller = { store, instance, _, types ->
    ModuleCompiler(module, types)
        .mapError { error -> error.toModuleTrapError() }
        .map { code -> ModuleLinker(store, instance, module, code) }
}
