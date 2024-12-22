package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.ast.module.Index.GlobalIndex
import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance
import kotlin.jvm.JvmInline

sealed interface VariableInstruction : ExecutionInstruction {
    @JvmInline
    value class LocalGet(val localIdx: Int) : VariableInstruction

    @JvmInline
    value class LocalSet(val localIdx: Int) : VariableInstruction

    @JvmInline
    value class LocalTee(val localIdx: Int) : VariableInstruction

    @JvmInline
    value class GlobalGet(val global: GlobalInstance) : VariableInstruction

    @JvmInline
    value class GlobalSet(val globalIdx: GlobalIndex) : VariableInstruction
}
