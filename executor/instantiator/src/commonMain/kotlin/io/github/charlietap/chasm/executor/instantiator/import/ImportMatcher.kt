package io.github.charlietap.chasm.executor.instantiator.import

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store

typealias ImportMatcher = (Store, Module, List<Triple<String, String, ExternalValue>>) -> Result<List<ExternalValue>, ModuleTrapError>
