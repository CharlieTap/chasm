package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Custom
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.ir.module.Custom as IRCustom
import io.github.charlietap.chasm.ir.value.NameValue as IRNameValue

internal fun CustomFactory(
    custom: Custom,
): IRCustom = CustomFactory(
    custom = custom,
    nameValueFactory = ::NameValueFactory,
)

internal inline fun CustomFactory(
    custom: Custom,
    nameValueFactory: IRFactory<NameValue, IRNameValue>,
): IRCustom {
    return IRCustom(
        name = nameValueFactory(custom.name),
        data = custom.data,
    )
}
