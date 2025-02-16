package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.ir.type.RecursiveType as IRRecursiveType
import io.github.charlietap.chasm.ir.type.SubType as IRSubType

internal fun RecursiveTypeFactory(
    recursiveType: RecursiveType,
): IRRecursiveType = RecursiveTypeFactory(
    recursiveType = recursiveType,
    subTypeFactory = ::SubTypeFactory,
)

internal inline fun RecursiveTypeFactory(
    recursiveType: RecursiveType,
    subTypeFactory: IRFactory<SubType, IRSubType>,
): IRRecursiveType {
    return IRRecursiveType(
        subTypes = recursiveType.subTypes.map(subTypeFactory),
        state = recursiveType.state,
    )
}
