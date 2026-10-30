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
}
