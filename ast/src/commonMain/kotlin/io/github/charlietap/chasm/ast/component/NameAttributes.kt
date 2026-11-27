package io.github.charlietap.chasm.ast.component

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.value.NameValue

@InternalChasmApi
data class NameAttributes(
    val name: NameValue,
    val attributes: List<ExternalAttribute> = emptyList(),
)

@InternalChasmApi
sealed interface ExternalAttribute {

    data class Implements(val interfaceName: String) : ExternalAttribute

    data class VersionSuffix(val suffix: String) : ExternalAttribute

    data class ExternalId(val id: NameValue) : ExternalAttribute
}
