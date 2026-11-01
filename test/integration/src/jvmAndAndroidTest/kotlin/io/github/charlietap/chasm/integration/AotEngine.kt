@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.aot.CompileConfig
import io.github.charlietap.chasm.aot.CompileError.Stage
import io.github.charlietap.chasm.aot.CompileOutput
import io.github.charlietap.chasm.aot.CompileResult
import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.aot.compile
import io.github.charlietap.chasm.aot.instance
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import io.github.charlietap.chasm.embedding.invoke as embeddingInvoke

class AotEngine : Engine {

    override fun name(): String = "aot"

    override fun instantiate(
        bytes: ByteArray,
        store: Store,
        imports: List<Import>,
        config: RuntimeConfig,
    ): ChasmResult<Instance, ChasmError> {
        val classes = CompiledClasses()
        return when (val result = compile(bytes, classes, CompileConfig(PACKAGE_NAME))) {
            is CompileResult.Success -> instance(store, classes.module(result.artifact.moduleClassName), imports, config)
            is CompileResult.Error -> when (result.error.stage) {
                Stage.DECODE -> ChasmResult.Error(ChasmError.DecodeError(result.error.error))
                Stage.VALIDATION -> ChasmResult.Error(ChasmError.ValidationError(result.error.error))
                else -> error("Compiling the module ahead of time failed: ${result.error}")
            }
        }
    }

    override fun invoke(
        store: Store,
        instance: Instance,
        name: String,
        arguments: List<ExecutionValue>,
    ): ChasmResult<List<ExecutionValue>, ChasmError> = embeddingInvoke(store, instance, name, arguments)

    private companion object {
        const val PACKAGE_NAME = "io.github.charlietap.chasm.integration.compiled"
    }
}

private class CompiledClasses : ClassLoader(CompiledClasses::class.java.classLoader), CompileOutput {

    private val classes = mutableMapOf<String, ByteArray>()
    private val resources = mutableMapOf<String, ByteArray>()

    override fun classFile(
        internalName: String,
        bytes: ByteArray,
    ) {
        classes[internalName] = bytes
    }

    override fun resource(
        path: String,
        write: (OutputStream) -> Unit,
    ) {
        resources[path] = ByteArrayOutputStream().also(write).toByteArray()
    }

    fun module(
        className: String,
    ): CompiledModule = loadClass(className).getField("INSTANCE").get(null) as CompiledModule

    override fun findClass(
        name: String,
    ): Class<*> {
        val bytes = classes[name.replace('.', '/')] ?: throw ClassNotFoundException(name)
        return defineClass(name, bytes, 0, bytes.size)
    }

    override fun getResourceAsStream(
        name: String,
    ): InputStream? = resources[name.removePrefix("/")]?.inputStream() ?: super.getResourceAsStream(name)
}
