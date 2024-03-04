package io.github.charlietap.chasm.import

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue

internal typealias ImportMatcher = (Module, List<Import>) -> Result<List<ExternalValue>, InstantiationError>
