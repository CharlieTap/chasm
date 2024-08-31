package io.github.charlietap.chasm.script.action

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.sweet.lib.command.Command
import kotlin.jvm.JvmInline

sealed interface ActionResult {

    @JvmInline
    value class Success(val value: List<Value>) : ActionResult

    data class Failure(
        val command: Command,
        val reason: String,
        val error: ChasmError? = null,
    ) : ActionResult
}
