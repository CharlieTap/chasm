package io.github.charlietap.sweet.lib.action

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("get")
data class GetAction(
    val field: String,
    @SerialName("module")
    val moduleName: String? = null,
): Action
