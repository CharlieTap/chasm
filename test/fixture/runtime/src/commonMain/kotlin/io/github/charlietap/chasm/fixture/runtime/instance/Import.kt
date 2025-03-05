package io.github.charlietap.chasm.fixture.runtime.instance

import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.Import

fun import(
    moduleName: String = "",
    entityName: String = "",
    externalValue: ExternalValue = externalValue(),
) = Import(
    moduleName = moduleName,
    entityName = entityName,
    externalValue = externalValue,
)
