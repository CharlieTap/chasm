package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import kotlin.jvm.JvmInline

sealed interface AdminInstruction : LinkedInstruction {

    data object EndBlock : AdminInstruction

    data object EndFunction : AdminInstruction

    @JvmInline
    value class Handler(val handler: ExceptionHandler) : AdminInstruction

    data object Pause : AdminInstruction

    data object PauseIf : AdminInstruction
}
