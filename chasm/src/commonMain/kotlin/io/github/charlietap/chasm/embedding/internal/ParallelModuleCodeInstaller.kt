package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.ParallelModuleCompiler
import io.github.charlietap.chasm.executor.instantiator.ParallelModuleCodeInstaller

internal fun ParallelModuleCodeInstaller(
    module: Module,
): ParallelModuleCodeInstaller =
    { store, instance, runtimeTypes, types, taskExecutor ->
        ParallelModuleCompiler(
            store = store,
            module = module,
            instance = instance,
            runtimeTypes = runtimeTypes,
            types = types,
            taskExecutor = taskExecutor,
        )
    }
