package io.github.charlietap.chasm.fixture.runtime.instance

import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import io.github.charlietap.chasm.runtime.instance.ExportInstance
import io.github.charlietap.chasm.runtime.instance.ExternalValue

fun exportInstance(
    name: NameValue = nameValue(),
    value: ExternalValue = externalValue(),
) = ExportInstance(
    name = name,
    value = value,
)
