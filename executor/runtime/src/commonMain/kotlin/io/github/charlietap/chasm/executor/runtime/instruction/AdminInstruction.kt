package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import kotlin.jvm.JvmInline

sealed interface AdminInstruction : ExecutionInstruction {

    @JvmInline
    value class Frame(val frame: ActivationFrame) : AdminInstruction

    @JvmInline
    value class Label(val label: Stack.Entry.Label) : AdminInstruction

    @JvmInline
    value class Handler(val handler: ExceptionHandler) : AdminInstruction
}
