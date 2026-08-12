package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest
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
    value class RefTest(val typeTest: ReferenceTypeTest) : ReferenceInstruction

    @JvmInline
    value class RefCast(val typeTest: ReferenceTypeTest) : ReferenceInstruction
}
