package io.github.charlietap.chasm.aot.internal

import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.aot.CompiledModuleMetadata
import io.github.charlietap.chasm.declarations.ModuleDeclarations

internal class AotModule(
    val compiled: CompiledModule,
    val metadata: CompiledModuleMetadata,
    val declarations: ModuleDeclarations,
)
