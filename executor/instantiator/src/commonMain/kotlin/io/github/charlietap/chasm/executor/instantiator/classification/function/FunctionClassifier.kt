package io.github.charlietap.chasm.executor.instantiator.classification.function

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.instantiator.classification.ClassifiedExternalValue
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias FunctionClassifier = (Store, ExternalValue.Function) -> Result<ClassifiedExternalValue, ModuleTrapError>
