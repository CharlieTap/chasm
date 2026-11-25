package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.declarations.DataSource
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation

interface CompiledModule {

    @InternalChasmApi
    fun runtimeAbi(): Int

    @InternalChasmApi
    fun target(): Int

    @InternalChasmApi
    fun byteArrayBaseOffset(): Long

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
    fun classes(): Array<String>

    @InternalChasmApi
    fun initializesEagerly(): Boolean

    @InternalChasmApi
    fun buildDeclarations(data: DataSource): ModuleDeclarations

    @InternalChasmApi
    fun structLanes(): LongArray

    @InternalChasmApi
    fun newStruct(
        type: Int,
        rtt: Any,
    ): Any

    @InternalChasmApi
    fun install(installation: CodeInstallation)
}
