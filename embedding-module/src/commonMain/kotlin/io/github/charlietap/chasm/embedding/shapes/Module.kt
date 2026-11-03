package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.declarations.moduleInfo
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.ast.module.Module as InternalModule

class Module internal constructor(
    internal val config: ModuleConfig,
    internal val module: InternalModule,
    /** Null for streamed modules. */
    internal val binary: ByteArray? = null,
    internal val validated: Boolean = false,
) {
    internal val declarations: ModuleDeclarations by lazy {
        ModuleDeclarationsFactory(module)
    }

    private val info by lazy {
        moduleInfo(declarations)
    }

    val imports by lazy {
        info.imports
    }

    val exports by lazy {
        info.exports
    }
}

@InternalChasmApi
fun _module(
    config: ModuleConfig,
    module: InternalModule,
    binary: ByteArray? = null,
    validated: Boolean = false,
): Module = Module(config, module, binary, validated)

@InternalChasmApi
fun Module._config(): ModuleConfig = config

@InternalChasmApi
fun Module._module(): InternalModule = module

@InternalChasmApi
fun Module._binary(): ByteArray? = binary

@InternalChasmApi
fun Module._validated(): Boolean = validated

@InternalChasmApi
fun Module._declarations(): ModuleDeclarations = declarations
