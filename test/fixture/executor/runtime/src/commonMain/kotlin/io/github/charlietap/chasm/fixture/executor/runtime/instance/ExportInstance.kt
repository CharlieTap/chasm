package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.ExportInstance
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.fixture.ir.value.nameValue
import io.github.charlietap.chasm.ir.value.NameValue

fun exportInstance(
    name: NameValue = nameValue(),
    value: ExternalValue = externalValue(),
) = ExportInstance(
    name = name,
    value = value,
)
