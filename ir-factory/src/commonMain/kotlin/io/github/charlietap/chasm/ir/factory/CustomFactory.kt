package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Custom
import io.github.charlietap.chasm.ast.module.NameData
import io.github.charlietap.chasm.ast.module.Uninterpreted
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
    return when (custom) {
        is NameData -> IRCustom(
            name = IRNameValue("name"),
            data = ubyteArrayOf(),
        )
        is Uninterpreted -> IRCustom(
            name = nameValueFactory(custom.name),
            data = custom.data,
        )
    }
}
