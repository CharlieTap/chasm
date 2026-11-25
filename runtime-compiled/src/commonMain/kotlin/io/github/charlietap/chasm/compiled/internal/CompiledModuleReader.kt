package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.compiled.CompiledModule
import io.github.charlietap.chasm.compiled.format.ArtifactTarget
import io.github.charlietap.chasm.compiled.format.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.compiled.format.UNPINNED_OFFSET
import io.github.charlietap.chasm.compiled.format.dataResource
import io.github.charlietap.chasm.declarations.moduleInfo
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._module

internal fun compiledModuleReader(): CompiledModuleReader {
    return CompiledModuleReader(
        abi = COMPILED_RUNTIME_ABI,
        target = platformTarget(),
        byteArrayLayout = VmByteArrayLayout(),
        ownership = ClassOwnership(),
        settings = RuntimeSettings(),
        checks = ResourceChecks(),
        initializer = SharedCodeClassInitializer(),
    )
}

internal expect fun platformTarget(): ArtifactTarget

internal class CompiledModuleReader(
    private val abi: Int,
    private val target: ArtifactTarget,
    private val byteArrayLayout: ByteArrayLayout,
    private val ownership: ClassOwnership,
    private val settings: RuntimeSettings,
    private val checks: ResourceChecks,
    private val initializer: CodeClassInitializer,
) {

    @Suppress("TooGenericExceptionCaught")
    fun read(
        compiled: CompiledModule,
        resources: ModuleResources,
        artifact: ClassLoader?,
    ): ChasmResult<Module, ModuleError> {

        val compiledAbi = try {
            compiled.runtimeAbi()
        } catch (_: LinkageError) {
            return malformed("the module was compiled for an older runtime")
        }
        if (compiledAbi != abi) {
            return malformed("the module was compiled for runtime ABI $compiledAbi but this runtime supports $abi")
        }

        val compiledTarget = compiled.target()
        if (compiledTarget != target.code) {
            return malformed("the module was compiled for ${name(compiledTarget)} but this runtime runs on ${name(target.code)}")
        }

        val eagerly = compiled.initializesEagerly()
        val classes = if (artifact != null || eagerly) compiled.classes().asList() else emptyList()
        if (artifact != null) {
            val foreign = ownership.foreignClass(classes, artifact)
            if (foreign != null) {
                return malformed(
                    "class $foreign of the module is missing from the input, does not link, or is defined by another class loader",
                )
            }
        }

        val pinned = compiled.byteArrayBaseOffset()
        if (pinned != UNPINNED_OFFSET) {
            val vmOffset = byteArrayLayout.baseOffset()
            if (pinned != vmOffset) {
                return malformed("the module reads byte arrays at offset $pinned but this VM keeps them at $vmOffset")
            }
        }

        val config = settings.config(compiled) ?: return malformed("the module's settings are invalid")
        val root = compiled.resourceRoot()

        val declarations = try {
            compiled.buildDeclarations(resources.data(dataResource(root)))
        } catch (exception: Exception) {
            return malformed(exception.message ?: exception.toString())
        } catch (failure: LinkageError) {
            return malformed("the module's declarations do not link against this runtime: $failure")
        }

        val incomplete = checks.failure(resources, root, declarations, config.stacktraces)
        if (incomplete != null) {
            return malformed(incomplete)
        }

        val info = moduleInfo(declarations)
        val eagerClasses = EagerClasses(compiled.javaClass.classLoader, if (eagerly) classes else emptyList(), initializer)
        val payload = CompiledModulePayload(compiled, config, declarations, root, resources, eagerClasses)

        return ChasmResult.Success(_module(info.imports, info.exports, payload))
    }

    private fun name(code: Int): String {
        return when (code) {
            ArtifactTarget.HOTSPOT.code -> "HotSpot"
            ArtifactTarget.ART.code -> "ART"
            else -> "target $code"
        }
    }
}
