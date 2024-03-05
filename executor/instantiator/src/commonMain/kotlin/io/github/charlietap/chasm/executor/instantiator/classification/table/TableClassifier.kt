package io.github.charlietap.chasm.executor.instantiator.classification.table

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.instantiator.classification.ClassifiedExternalValue
import io.github.charlietap.chasm.executor.runtime.error.ModuleRuntimeError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias TableClassifier = (Store, ExternalValue.Table) -> Result<ClassifiedExternalValue, ModuleRuntimeError>
