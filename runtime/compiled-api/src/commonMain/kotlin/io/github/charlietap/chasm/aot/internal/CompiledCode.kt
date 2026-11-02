package io.github.charlietap.chasm.aot.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation

@InternalChasmApi
interface CompiledCode {

    fun buildDeclarations(): ModuleDeclarations

    fun install(
        installation: CodeInstallation,
    )

    /** The resource holding the module's trap sites, or null when it was compiled without them. */
    fun trapSitesResource(): String?

    fun runtimeAbi(): Int

    fun meterFuel(): Boolean

    fun interruptible(): Boolean

    fun stacktraces(): Boolean

    fun initialFuel(): Long

    fun gcStrategy(): Int

    fun gcThresholdKind(): Int

    fun gcThresholdValue(): Int

    fun gcHeapBytes(): Long

    fun memoryPages(): Int

    fun tableElements(): Int

    fun trapOnGrowFailure(): Boolean

    fun stackBytes(): Long
}
