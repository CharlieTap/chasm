package io.github.charlietap.chasm.predecoder

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError

typealias Predecoder<I, O> = (PredecodingContext, I) -> Result<O, ModuleTrapError>
