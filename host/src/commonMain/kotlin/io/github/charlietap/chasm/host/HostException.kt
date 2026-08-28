package io.github.charlietap.chasm.host

import kotlin.jvm.JvmInline

/**
 * An encoded WebAssembly exception belonging to one Chasm store.
 *
 * This is not a Kotlin [Throwable]. It remains valid only while reachable by
 * the guest or rooted through [HostReferences].
 */
@JvmInline
value class HostException(val rawReference: HostReference)
