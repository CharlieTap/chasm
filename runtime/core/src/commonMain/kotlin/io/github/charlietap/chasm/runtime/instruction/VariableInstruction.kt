package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import kotlin.jvm.JvmInline

sealed interface VariableInstruction : LinkedInstruction {
    @JvmInline
    value class LocalGet(val localIdx: Int) : VariableInstruction

    @JvmInline
    value class LocalSet(val localIdx: Int) : VariableInstruction

    @JvmInline
    value class LocalTee(val localIdx: Int) : VariableInstruction

    @JvmInline
    value class GlobalGet(val global: GlobalInstance) : VariableInstruction

    @JvmInline
    value class GlobalSet(val global: GlobalInstance) : VariableInstruction
}
