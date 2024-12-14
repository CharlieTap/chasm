package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.Import

fun import(
    moduleName: String = "",
    entityName: String = "",
    externalValue: ExternalValue = externalValue(),
) = Import(
    moduleName = moduleName,
    entityName = entityName,
    externalValue = externalValue,
)
