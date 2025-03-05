package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.fixture.ir.value.nameValue
import io.github.charlietap.chasm.ir.value.NameValue
import io.github.charlietap.chasm.runtime.instance.ExportInstance
import io.github.charlietap.chasm.runtime.instance.ExternalValue

fun exportInstance(
    name: NameValue = nameValue(),
    value: ExternalValue = externalValue(),
) = ExportInstance(
    name = name,
    value = value,
)
