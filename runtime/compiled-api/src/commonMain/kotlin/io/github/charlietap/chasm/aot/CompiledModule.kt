package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.CompiledCode
import io.github.charlietap.chasm.aot.internal.TrapLines
import io.github.charlietap.chasm.aot.internal.TrapSites
import io.github.charlietap.chasm.aot.internal.readTrapLines
import io.github.charlietap.chasm.aot.internal.readTrapSites
import io.github.charlietap.chasm.aot.internal.trapLinesResource
import io.github.charlietap.chasm.aot.internal.trapSitesResource
import io.github.charlietap.chasm.declarations.ModuleDeclarations

@ExperimentalChasmApi
@OptIn(InternalChasmApi::class)
class CompiledModule internal constructor(
    @property:InternalChasmApi val code: CompiledCode,
) {
    @InternalChasmApi
    @Suppress("TooGenericExceptionCaught")
    val declarations: Result<ModuleDeclarations> by lazy {
        try {
            Result.success(code.buildDeclarations())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    // The package the module was compiled into, which its code's class names keep unless a shrinker renames them.
    @InternalChasmApi
    val packageName: String
        get() = code.trapSitesResource()?.substringBeforeLast('/')?.replace('/', '.')
            ?: code.javaClass.name.substringBeforeLast('.', "")

    // Null when the module was compiled without trap diagnostics.
    @InternalChasmApi
    val trapSites: TrapSites? by lazy {
        val path = code.trapSitesResource() ?: return@lazy null
        code.javaClass.classLoader?.getResourceAsStream(path)?.use(::readTrapSites)
    }

    // Present when a shrinker renumbered the compiled methods' lines. Only an Android app's assets can carry it, as it is
    // made from the shrinker's mapping after the app's classes and resources are shrunk.
    @InternalChasmApi
    val trapLines: TrapLines? by lazy {
        val packagePath = code.trapSitesResource()?.substringBeforeLast('/') ?: return@lazy null
        code.javaClass.classLoader?.getResourceAsStream(ANDROID_ASSETS + trapLinesResource(packagePath))?.use(::readTrapLines)
    }

    private companion object {
        const val ANDROID_ASSETS = "assets/"
    }
}
