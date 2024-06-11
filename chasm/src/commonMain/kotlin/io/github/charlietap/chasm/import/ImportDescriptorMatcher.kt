package io.github.charlietap.chasm.import

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.ast.module.Import as ModuleImport

internal typealias ImportDescriptorMatcher = (Store, Module, ModuleImport.Descriptor, ExternalValue) -> Result<Boolean, ModuleTrapError>
