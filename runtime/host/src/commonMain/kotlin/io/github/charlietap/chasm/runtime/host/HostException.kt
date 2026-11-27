package io.github.charlietap.chasm.runtime.host

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.jvm.JvmInline

/**
 * An encoded WebAssembly exception belonging to one Chasm store.
 *
 * This is not a Kotlin [Throwable]. It remains valid only while reachable by
 * the guest or rooted through [HostReferences].
 */
@InternalChasmApi
@JvmInline
value class HostException(val rawReference: HostReference)
