package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index
import kotlin.jvm.JvmInline

sealed interface FusedDestination {
    @JvmInline
    value class LocalSet(val index: Index.LocalIndex) : FusedDestination

    @JvmInline
    value class FrameSlot(val offset: Int) : FusedDestination

    data object ValueStack : FusedDestination
}
