package io.github.charlietap.chasm.script.action

import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.sweet.lib.command.Command
import kotlin.jvm.JvmInline

sealed interface ActionResult {

    @JvmInline
    value class Success(val value: List<ExecutionValue>) : ActionResult

    data class Failure(
        val command: Command,
        val reason: String,
        val error: ChasmError? = null,
    ) : ActionResult
}
