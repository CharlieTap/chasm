package io.github.charlietap.sweet.lib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ModuleType {
    @SerialName("binary")
    Binary,
    @SerialName("text")
    Text,
}
