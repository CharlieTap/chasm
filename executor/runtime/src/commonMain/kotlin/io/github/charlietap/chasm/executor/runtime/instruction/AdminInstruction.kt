package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import kotlin.jvm.JvmInline

sealed interface AdminInstruction : ExecutionInstruction {

    @JvmInline
    value class Frame(val frame: Stack.Entry.ActivationFrame) : AdminInstruction

    @JvmInline
    value class Label(val label: Stack.Entry.Label) : AdminInstruction

    @JvmInline
    value class Handler(val handler: ExceptionHandler) : AdminInstruction
}
