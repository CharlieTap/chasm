package io.github.charlietap.chasm.compiled.script.action

import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.sweet.lib.command.Command
import kotlin.jvm.JvmInline

sealed interface ActionResult {

    @JvmInline
    value class Success(
        val value: List<ExecutionValue>,
    ) : ActionResult

    data class Failure(
        val command: Command,
        val reason: String,
        val error: ExecutionError? = null,
    ) : ActionResult
}
