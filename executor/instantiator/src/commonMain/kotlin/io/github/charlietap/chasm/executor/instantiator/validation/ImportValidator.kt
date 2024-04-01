package io.github.charlietap.chasm.executor.instantiator.validation

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.executor.instantiator.classification.ClassifiedExternalValue
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance

internal typealias ImportValidator = (ModuleInstance, Import, ClassifiedExternalValue) -> Result<Unit, InstantiationError.UnexpectedImport>
