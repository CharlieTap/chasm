package io.github.charlietap.chasm.jit.internal

internal class CompileConfig(
    val meterFuel: Boolean,
    val interruptible: Boolean,
    val stacktraces: Boolean,
) {
    val devirtualiseIndirectCalls: Boolean
        get() = !stacktraces && !meterFuel && !interruptible
}
