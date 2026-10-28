package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.backend.chasm.ParallelModuleLinker
import io.github.charlietap.chasm.compiler.frontend.ParallelModuleCompiler
import io.github.charlietap.chasm.embedding.ext.toModuleTrapError
import io.github.charlietap.chasm.executor.instantiator.ParallelModuleCodeInstaller

internal fun ParallelModuleCodeInstaller(
    module: Module,
): ParallelModuleCodeInstaller =
    { store, instance, _, types, taskExecutor ->
        ParallelModuleCompiler(module, types, taskExecutor)
            .mapError { error -> error.toModuleTrapError() }
            .map { code -> ParallelModuleLinker(store, instance, module, code, taskExecutor) }
    }
