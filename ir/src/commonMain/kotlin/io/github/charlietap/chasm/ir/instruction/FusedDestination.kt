package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index
import kotlin.jvm.JvmInline

sealed interface FusedDestination {
    data object ValueStack : FusedDestination

    @JvmInline
    value class LocalSet(val index: Index.LocalIndex) : FusedDestination

    @JvmInline
    value class GlobalSet(val index: Index.GlobalIndex) : FusedDestination
}
