package io.github.charlietap.chasm.compiled.code

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiled.CompiledModule
import io.github.charlietap.chasm.compiled.internal.platformTarget
import io.github.charlietap.chasm.compiled.module
import io.github.charlietap.chasm.compiled.store
import io.github.charlietap.chasm.compiler.backend.jvm.JvmModule
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOutput
import io.github.charlietap.chasm.compiler.backend.jvm.ModuleLowerer
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlinx.coroutines.runBlocking
import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.CheckClassAdapter
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.test.assertEquals
import io.github.charlietap.chasm.embedding.shapes.Module as EmbeddingModule
import io.github.charlietap.chasm.embedding.store as interpreterStore

internal fun fixture(
    name: String,
): ByteArray =
    checkNotNull(Generated::class.java.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
        .use { it.readBytes() }

internal fun longFunctionModule(steps: Int): ByteArray {

    val body = ArrayList<Byte>()
    body.bytes(0x00, 0x20, 0x00, 0x41, 0x01, 0x20, 0x01, 0x6e, 0x6a, 0x21, 0x00)
    repeat(steps) { step ->
        if (step % 8 == 7) {
            body.bytes(0x20, 0x00, 0x41, 0x00, 0x48, 0x04, 0x40, 0x20, 0x00, 0x41, 0x03, 0x6a, 0x21, 0x00, 0x0b)
        } else {
            body.bytes(0x20, 0x00, 0x41, step % 5 + 1, 0x6a, 0x21, 0x00)
        }
    }
    body.bytes(0x20, 0x00, 0x0b)

    val module = ArrayList<Byte>()
    module.bytes(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00)
    module.section(1, listOf(0x01, 0x60, 0x02, 0x7f, 0x7f, 0x01, 0x7f).map(Int::toByte))
    module.section(3, listOf<Byte>(0x01, 0x00))
    module.section(7, listOf(0x01, 0x03, 'r'.code, 'u'.code, 'n'.code, 0x00, 0x00).map(Int::toByte))
    module.section(
        10,
        ArrayList<Byte>().apply {
            bytes(0x01)
            leb(body.size)
            addAll(body)
        },
    )

    return module.toByteArray()
}

private fun ArrayList<Byte>.bytes(vararg values: Int) {
    values.forEach { value -> add(value.toByte()) }
}

private fun ArrayList<Byte>.leb(value: Int) {

    var rest = value
    do {
        val low = rest and 0x7f
        rest = rest ushr 7
        add((if (rest != 0) low or 0x80 else low).toByte())
    } while (rest != 0)
}

private fun ArrayList<Byte>.section(
    id: Int,
    content: List<Byte>,
) {
    bytes(id)
    leb(content.size)
    addAll(content)
}

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

    fun compiledModule(): EmbeddingModule {
        return module(GeneratedClassLoader(this).loadClass(module.moduleClass).getField("COMPILED").get(null) as CompiledModule).expect("loads")
    }
}

internal fun generate(
    bytes: ByteArray,
    options: JvmOptions = JvmOptions(),
): Generated {
    val module = decodeNamedModule(bytes)
    val code = ModuleCompiler(
        module = module,
        types = module.typeResolver(),
        recordProvenance = false,
        devirtualiseIndirectCalls = !options.stacktraces && !options.meterFuel && !options.interruptible,
    ).getOrThrow { IllegalStateException(it.toString()) }
    val generated = Generated()
    generated.module = ModuleLowerer(module, code, options.copy(target = platformTarget()), generated, ModuleDeclarationsFactory(module))
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
    val store: Store = interpreterStore()
    val instance: Instance = runBlocking { instance(store, module(bytes).expect("decodes")) }.expect("instantiates")

    fun invoke(
        name: String,
        vararg args: ExecutionValue,
    ): ChasmResult<List<ExecutionValue>, *> =
        invoke(instance, name, args.toList())

    fun call(
        name: String,
        vararg args: ExecutionValue,
    ): List<ExecutionValue> =
        invoke(instance, name, args.toList()).expect(name)
}

internal class CompiledFixture(
    bytes: ByteArray,
    options: JvmOptions = JvmOptions(),
) {
    val generated = generate(bytes, options)
    val module: EmbeddingModule = generated.compiledModule()
    val store: Store = store(module)
    val instance: Instance = runBlocking { instance(store, module) }.expect("instantiates")

    fun invoke(
        name: String,
        vararg args: ExecutionValue,
    ): ChasmResult<List<ExecutionValue>, *> =
        invoke(instance, name, args.toList())

    fun call(
        name: String,
        vararg args: ExecutionValue,
    ): List<ExecutionValue> =
        invoke(instance, name, args.toList()).expect(name)
}
