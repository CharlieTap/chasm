package io.github.charlietap.chasm.executor.instantiator.predecoding

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError

internal typealias Predecoder<I, O> = (InstantiationContext, I) -> Result<O, ModuleTrapError>
