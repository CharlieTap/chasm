package io.github.charlietap.chasm.ast.instruction

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.HeapType
import kotlin.jvm.JvmInline

sealed interface ReferenceInstruction : Instruction {
    @JvmInline
    value class RefNull(val type: HeapType) : ReferenceInstruction

    data object RefIsNull : ReferenceInstruction

    data object RefAsNonNull : ReferenceInstruction

    @JvmInline
    value class RefFunc(val funcIdx: Index.FunctionIndex) : ReferenceInstruction
}
