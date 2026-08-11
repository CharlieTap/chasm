package io.github.charlietap.sweet.lib.value

import kotlinx.serialization.Serializable

@Serializable(with = ComponentRecordFieldSerializer::class)
data class ComponentRecordField(
    val name: String,
    val value: Value,
)
