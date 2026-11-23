package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation

interface CompiledModule {

    @InternalChasmApi
    fun runtimeAbi(): Int

    @InternalChasmApi
    fun interrupts(): Boolean

    @InternalChasmApi
    fun stacktraces(): Boolean

    @InternalChasmApi
    fun fuel(): Long

    @InternalChasmApi
    fun linearMemoryBytes(): Long

    @InternalChasmApi
    fun gcHeapBytes(): Long

    @InternalChasmApi
    fun tableBytes(): Long

    @InternalChasmApi
    fun stackBytes(): Long

    @InternalChasmApi
    fun trapOnGrowthFailure(): Boolean

    @InternalChasmApi
    fun resourceRoot(): String

    @InternalChasmApi
    fun buildDeclarations(): ModuleDeclarations

    @InternalChasmApi
    fun install(installation: CodeInstallation)
}
