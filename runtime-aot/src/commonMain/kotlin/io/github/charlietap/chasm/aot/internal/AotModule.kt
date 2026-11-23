package io.github.charlietap.chasm.aot.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.declarations.ModuleDeclarations

@OptIn(InternalChasmApi::class)
internal class AotModule(
    val compiled: CompiledModule,
    val config: RuntimeConfig,
    val declarations: ModuleDeclarations,
    resourceRoot: String,
) {

    val classPrefix = "${resourceRoot.replace('/', '.')}\$"

    val trapSites: TrapSites? by lazy {

        if (!config.stacktraces) {
            return@lazy null
        }

        compiled.javaClass.classLoader?.getResourceAsStream(trapSitesResource(resourceRoot))?.use(::readTrapSites)
    }

    val trapLines: TrapLines? by lazy {

        if (!config.stacktraces) {
            return@lazy null
        }

        val loader = compiled.javaClass.classLoader ?: return@lazy null
        val path = trapLinesResource(resourceRoot)
        val stream = loader.getResourceAsStream("assets/$path") ?: loader.getResourceAsStream(path)
        stream?.use(::readTrapLines)
    }
}
