package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.Stack
import kotlin.jvm.JvmInline

sealed interface AdminInstruction : ExecutionInstruction {

    @JvmInline
    value class Frame(val frame: Stack.Entry.ActivationFrame) : AdminInstruction

    @JvmInline
    value class Label(val label: Stack.Entry.Label) : AdminInstruction
}
