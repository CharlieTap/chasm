package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.type.HeapType
import io.github.charlietap.chasm.ir.type.ReferenceType
import kotlin.jvm.JvmInline

sealed interface ReferenceInstruction : Instruction {
    @JvmInline
    value class RefNull(val type: HeapType) : ReferenceInstruction

    data object RefIsNull : ReferenceInstruction

    data object RefAsNonNull : ReferenceInstruction

    @JvmInline
    value class RefFunc(val funcIdx: Index.FunctionIndex) : ReferenceInstruction

    data object RefEq : ReferenceInstruction

    @JvmInline
    value class RefTest(val referenceType: ReferenceType) : ReferenceInstruction

    @JvmInline
    value class RefCast(val referenceType: ReferenceType) : ReferenceInstruction
}
