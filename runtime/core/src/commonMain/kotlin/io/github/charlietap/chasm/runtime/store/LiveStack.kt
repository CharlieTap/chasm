package io.github.charlietap.chasm.runtime.store

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.stack.ValueStack
import kotlin.jvm.JvmField

@PublishedApi
internal class LiveStack(
    @JvmField val stack: ValueStack,
    @JvmField val next: LiveStack?,
)

@InternalChasmApi
inline fun <T> Store.withLiveStack(
    stack: ValueStack,
    block: () -> T,
): T {

    val previous = liveStacks
    liveStacks = LiveStack(stack, previous)

    try {
        return block()
    } finally {
        liveStacks = previous
    }
}
