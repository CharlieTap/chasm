package io.github.charlietap.chasm.runtime.function

import kotlin.jvm.JvmInline

/** Precomputed initialization shape for a function's defined locals. */
sealed interface LocalInitialization {

    data object None : LocalInitialization

    data object Zero1 : LocalInitialization

    data object Zero2 : LocalInitialization

    data object Zero3 : LocalInitialization

    data object Zero4 : LocalInitialization

    @JvmInline
    value class ZeroRange(val count: Int) : LocalInitialization

    class ConstantStores(val values: LongArray) : LocalInitialization
}

fun classifyLocalInitialization(values: LongArray): LocalInitialization {
    if (values.isEmpty()) return LocalInitialization.None
    var index = 0
    while (index < values.size && values[index] == 0L) index++
    if (index == values.size) {
        return when (values.size) {
            1 -> LocalInitialization.Zero1
            2 -> LocalInitialization.Zero2
            3 -> LocalInitialization.Zero3
            4 -> LocalInitialization.Zero4
            else -> LocalInitialization.ZeroRange(values.size)
        }
    }
    return LocalInitialization.ConstantStores(values)
}
