package io.github.charlietap.chasm.script.action

import io.github.charlietap.chasm.embedding.compiled.value.ExecutionValue
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.sweet.lib.command.Command
import kotlin.jvm.JvmInline

sealed interface ActionResult {

    @JvmInline
    value class Success(val value: List<ExecutionValue>) : ActionResult

    data class Failure(
        val command: Command,
        val reason: String,
        val error: InvocationError<ExceptionReference>? = null,
    ) : ActionResult
}
