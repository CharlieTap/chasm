package io.github.charlietap.chasm.executor.instantiator.classification.memory

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.instantiator.classification.ClassifiedExternalValue
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias MemoryClassifier = (Store, ExternalValue.Memory) -> Result<ClassifiedExternalValue, InstantiationError.ImportLookupFailed>
