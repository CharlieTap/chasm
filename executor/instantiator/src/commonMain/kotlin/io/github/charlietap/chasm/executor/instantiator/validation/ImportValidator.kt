package io.github.charlietap.chasm.executor.instantiator.validation

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.instantiator.classification.ClassifiedExternalValue
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError

internal typealias ImportValidator = (Module, Import, ClassifiedExternalValue) -> Result<Unit, InstantiationError.UnexpectedImport>
