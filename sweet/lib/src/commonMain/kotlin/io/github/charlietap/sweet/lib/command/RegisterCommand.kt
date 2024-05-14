package io.github.charlietap.sweet.lib.command

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("register")
data class RegisterCommand(
    override val line: Int,
    val name: String? = null,
    @SerialName("as")
    val registerAs: String,
): Command
