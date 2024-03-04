package io.github.charlietap.chasm.ast.instruction

import io.github.charlietap.chasm.ast.type.ReferenceType
import kotlin.jvm.JvmInline

sealed interface ReferenceInstruction : Instruction {
    @JvmInline
    value class RefNull(val type: ReferenceType) : ReferenceInstruction

    data object RefIsNull : ReferenceInstruction

    @JvmInline
    value class RefFunc(val funcIdx: Index.FunctionIndex) : ReferenceInstruction
}
