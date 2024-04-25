package io.github.charlietap.chasm.fixture.instance

import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.executor.runtime.instance.ExportInstance
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.fixture.value.nameValue

fun exportInstance(
    name: NameValue = nameValue(),
    value: ExternalValue = externalValue(),
) = ExportInstance(
    name = name,
    value = value,
)
