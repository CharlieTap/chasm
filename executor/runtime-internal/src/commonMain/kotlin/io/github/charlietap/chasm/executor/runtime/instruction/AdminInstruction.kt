package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.executor.runtime.stack.ControlStack
import kotlin.jvm.JvmInline

sealed interface AdminInstruction : LinkedInstruction {

    @JvmInline
    value class Frame(val frame: ActivationFrame) : AdminInstruction

    @JvmInline
    value class Label(val label: ControlStack.Entry.Label) : AdminInstruction

    @JvmInline
    value class Handler(val handler: ExceptionHandler) : AdminInstruction
}
