package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.compiled.CompiledModule
import io.github.charlietap.chasm.compiled.format.TrapLines
import io.github.charlietap.chasm.compiled.format.TrapSites
import io.github.charlietap.chasm.compiled.format.readTrapLines
import io.github.charlietap.chasm.compiled.format.readTrapSites
import io.github.charlietap.chasm.compiled.format.trapLinesResource
import io.github.charlietap.chasm.compiled.format.trapSitesResource
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.declarations.ModuleDeclarations

internal class CompiledModulePayload(
    val compiled: CompiledModule,
    val config: RuntimeConfig,
    val declarations: ModuleDeclarations,
    val resourceRoot: String,
    private val resources: ModuleResources,
    val eagerClasses: EagerClasses,
) {

    val classPrefix = "${resourceRoot.replace('/', '.')}\$"

    val structLayouts: CompiledStructLayouts by lazy {
        CompiledStructLayouts(declarations.definedTypes, compiled)
    }

    val trapSites: TrapSites? by lazy {

        if (!config.stacktraces) {
            return@lazy null
        }

        resources.open(trapSitesResource(resourceRoot))?.use(::readTrapSites)
    }

    val trapLines: TrapLines? by lazy {

        if (!config.stacktraces) {
            return@lazy null
        }

        val path = trapLinesResource(resourceRoot)
        val stream = resources.open("assets/$path") ?: resources.open(path)
        stream?.use(::readTrapLines)
    }
}
