package io.github.charlietap.chasm.executor.instantiator.classification

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.ModuleRuntimeError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias ExternalValueClassifier = (Store, ExternalValue) -> Result<ClassifiedExternalValue, ModuleRuntimeError>
