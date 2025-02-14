package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.ast.type.ReferenceType
import kotlin.jvm.JvmInline

sealed interface ReferenceInstruction : LinkedInstruction {
    @JvmInline
    value class RefNull(val reference: Long) : ReferenceInstruction

    data object RefIsNull : ReferenceInstruction

    data object RefAsNonNull : ReferenceInstruction

    @JvmInline
    value class RefFunc(val reference: Long) : ReferenceInstruction

    data object RefEq : ReferenceInstruction

    @JvmInline
    value class RefTest(val referenceType: ReferenceType) : ReferenceInstruction

    @JvmInline
    value class RefCast(val referenceType: ReferenceType) : ReferenceInstruction
}
