package io.github.charlietap.sweet.lib.value

import kotlinx.serialization.Serializable

@Serializable(with = ComponentResultSerializer::class)
sealed interface ComponentResult {

    data class Ok(val payload: Value?) : ComponentResult

    data class Err(val payload: Value?) : ComponentResult
}
