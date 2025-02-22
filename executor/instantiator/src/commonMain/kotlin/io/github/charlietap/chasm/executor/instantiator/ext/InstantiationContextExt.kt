package io.github.charlietap.chasm.executor.instantiator.ext

import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.predecoder.PredecodingContext

internal inline fun InstantiationContext.asPredecodingContext() = PredecodingContext(
    instance = instance!!,
    store = store,
    instructionCache = instructionCache,
    types = types,
    unrollCache = unrollCache,
    loadCache = loadCache,
    storeCache = storeCache,
)
