@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import java.util.function.Consumer

internal class JitCompilation(
    val packageName: String,
    val chunkStarts: Map<Int, IntArray>,
    private val installer: Consumer<CodeInstallation>,
    private val inlineMemory: Boolean,
) {

    fun install(
        installation: CodeInstallation,
    ) {
        require(!inlineMemory || installation.memory(0).byteArray != null) {
            "memory 0 is not backed by a byte array the compiled code can access"
        }
        installer.accept(installation)
    }
}
