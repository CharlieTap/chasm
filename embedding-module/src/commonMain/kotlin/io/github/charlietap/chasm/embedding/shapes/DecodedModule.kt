package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.ast.module.Module as InternalModule

@InternalChasmApi
class DecodedModule internal constructor(
    val config: ModuleConfig,
    val module: InternalModule,
    val declarations: ModuleDeclarations,
    val binary: ByteArray?,
    val validated: Boolean,
)

@InternalChasmApi
fun _decodedModule(
    config: ModuleConfig,
    module: InternalModule,
    binary: ByteArray?,
    validated: Boolean,
): DecodedModule {
    return DecodedModule(config, module, ModuleDeclarationsFactory(module), binary, validated)
}
