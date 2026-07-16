package io.github.charlietap.chasm.runtime.component.resource

import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex

data class CanonicalResourceFunction(
    val kind: CanonicalResourceFunctionKind,
    val owner: RuntimeComponentInstanceIndex,
    val resourceType: RuntimeResourceTypeIndex,
)

enum class CanonicalResourceFunctionKind {
    ResourceNew,
    ResourceRep,
    ResourceDrop,
}
