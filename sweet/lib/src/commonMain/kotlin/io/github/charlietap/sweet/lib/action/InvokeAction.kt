package io.github.charlietap.sweet.lib.action

import io.github.charlietap.sweet.lib.value.Value
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("invoke")
data class InvokeAction(
    val field: String,
    val args: List<Value>,
    @SerialName("module")
    val moduleName: String? = null,
): Action
