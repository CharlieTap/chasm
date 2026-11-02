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
}
