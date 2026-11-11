@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.jit.internal._instance

@ExperimentalChasmApi
fun instance(
    store: Store,
    module: Module,
    imports: List<Import>,
    config: RuntimeConfig = RuntimeConfig(),
): ChasmResult<Instance, ChasmError.ExecutionError> = _instance(store, module, imports, config)

/**
 * Instantiates [module] like [instance], reusing compiled code from [cache] when an earlier run compiled
 * the same module with the same fuel and interrupt settings and [RuntimeConfig.debugInfo], and storing it
 * there otherwise.
 */
@ExperimentalChasmApi
fun instance(
    store: Store,
    module: Module,
    imports: List<Import>,
    config: RuntimeConfig,
    cache: JitCache,
): ChasmResult<Instance, ChasmError.ExecutionError> = _instance(store, module, imports, config, cache.asCompilationCache())
