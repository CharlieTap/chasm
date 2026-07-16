package io.github.charlietap.chasm.script.action

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.script.value.ScriptValue
import io.github.charlietap.chasm.type.component.ComponentValueType
import io.github.charlietap.sweet.lib.command.Command

sealed interface ActionResult {

    data class Success(
        val value: List<ScriptValue>,
        val componentResultType: ComponentValueType? = null,
    ) : ActionResult

    data class Failure(
        val command: Command,
        val reason: String,
        val error: ChasmError? = null,
    ) : ActionResult
}
