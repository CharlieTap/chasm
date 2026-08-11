package io.github.charlietap.sweet.lib.value

import kotlinx.serialization.Serializable

@Serializable
data class ComponentVariant(
    val case: String,
    val payload: Value? = null,
)
