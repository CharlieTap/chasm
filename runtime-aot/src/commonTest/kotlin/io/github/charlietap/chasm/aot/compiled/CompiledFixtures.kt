package io.github.charlietap.chasm.aot.compiled

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.aot.instance
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.backend.jvm.JvmModule
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOutput
import io.github.charlietap.chasm.compiler.backend.jvm.ModuleLowerer
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.validator.WasmModuleValidator
import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.CheckClassAdapter
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.test.assertEquals

internal fun fixture(
    name: String,
): ByteArray =
    checkNotNull(Generated::class.java.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
        .use { it.readBytes() }

private fun decodeModule(
    bytes: ByteArray,
    config: ModuleConfig = ModuleConfig(),
): Module {
    val module = WasmModuleDecoder(config, bytes).getOrThrow { IllegalStateException(it.toString()) }
    return WasmModuleValidator(config, module).getOrThrow { IllegalStateException(it.toString()) }
}

private fun decodeNamedModule(
    bytes: ByteArray,
): Module =
    runCatching { decodeModule(bytes, ModuleConfig(decodeNameSection = true)) }.getOrElse { decodeModule(bytes) }

internal class Generated : JvmOutput {
    val classes = LinkedHashMap<String, ByteArray>()
    val resources = LinkedHashMap<String, ByteArray>()
    lateinit var module: JvmModule

    override fun classFile(
        internalName: String,
        bytes: ByteArray,
    ) {
        check(classes.put(internalName, bytes) == null) { "class $internalName generated twice" }
    }

    override fun <T> resource(
        path: String,
        write: (OutputStream) -> T,
    ): T {
        val stream = ByteArrayOutputStream()
        val result = write(stream)
        check(resources.put(path, stream.toByteArray()) == null) { "resource $path generated twice" }
        return result
    }

    fun compiledModule(): CompiledModule =
        GeneratedClassLoader(this).loadClass(module.moduleClass).getField("INSTANCE").get(null) as CompiledModule
}

internal fun generate(
    bytes: ByteArray,
    options: JvmOptions = JvmOptions(),
): Generated {
    val module = decodeNamedModule(bytes)
    val code = ModuleCompiler(module).getOrThrow { IllegalStateException(it.toString()) }
    val generated = Generated()
    generated.module = ModuleLowerer(module, code, options, generated, ModuleDeclarationsFactory(module))
    return generated
}

internal class GeneratedClassLoader(
    private val generated: Generated,
) : ClassLoader(Generated::class.java.classLoader) {
    override fun findClass(
        name: String,
    ): Class<*> {
        val bytes = bytes(name) ?: throw ClassNotFoundException(name)
        return defineClass(name, bytes, 0, bytes.size)
    }

    fun bytes(
        name: String,
    ): ByteArray? = generated.classes[name.replace('.', '/')]

    override fun getResourceAsStream(
        name: String,
    ): InputStream? =
        generated.resources[name.removePrefix("/")]?.inputStream() ?: super.getResourceAsStream(name)
}

internal fun Generated.verify() {
    val loader = GeneratedClassLoader(this)
    classes.forEach { (name, bytes) ->
        val output = StringWriter()
        CheckClassAdapter.verify(ClassReader(bytes), loader, false, PrintWriter(output))
        assertEquals("", output.toString(), "$name does not verify")
    }
}

internal class InterpretedFixture(
    bytes: ByteArray,
) {
    val store: Store = store()
    val instance: Instance = instance(store, module(bytes).expect("decodes"), emptyList()).expect("instantiates")

    fun invoke(
        name: String,
        vararg args: ExecutionValue,
    ): ChasmResult<List<ExecutionValue>, *> =
        invoke(store, instance, name, args.toList())

    fun call(
        name: String,
        vararg args: ExecutionValue,
    ): List<ExecutionValue> =
        invoke(store, instance, name, args.toList()).expect(name)
}

internal class CompiledFixture(
    bytes: ByteArray,
    storeConfig: StoreConfig = StoreConfig(),
    options: JvmOptions = JvmOptions(meterFuel = storeConfig.meterFuel, interruptible = storeConfig.interruptible),
) {
    val generated = generate(bytes, options)
    val store: Store = store(storeConfig)
    val instance: Instance = instance(store, generated.compiledModule(), emptyList()).expect("instantiates")

    fun invoke(
        name: String,
        vararg args: ExecutionValue,
    ): ChasmResult<List<ExecutionValue>, *> =
        invoke(store, instance, name, args.toList())

    fun call(
        name: String,
        vararg args: ExecutionValue,
    ): List<ExecutionValue> =
        invoke(store, instance, name, args.toList()).expect(name)
}
