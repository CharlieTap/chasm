package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.ir.value.NameValue as IRNameValue

internal inline fun NameValueFactory(
    nameValue: NameValue,
): IRNameValue {
    return IRNameValue(
        name = nameValue.name,
    )
}
