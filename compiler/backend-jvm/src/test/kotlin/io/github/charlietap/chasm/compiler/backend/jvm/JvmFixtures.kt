package io.github.charlietap.chasm.compiler.backend.jvm

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.MethodNode
import java.io.ByteArrayOutputStream
import java.io.OutputStream

internal fun fixture(
    name: String,
): ByteArray =
    checkNotNull(JvmModule::class.java.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
        .use { it.readBytes() }

internal fun decodeModule(
    bytes: ByteArray,
    config: ModuleConfig = ModuleConfig(),
): Module {
    val module = WasmModuleDecoder(config, bytes).getOrThrow { IllegalStateException(it.toString()) }
    return WasmModuleValidator(config, module).getOrThrow { IllegalStateException(it.toString()) }
}

internal fun decodeNamedModule(
    bytes: ByteArray,
): Module =
    runCatching { decodeModule(bytes, ModuleConfig(decodeNameSection = true)) }.getOrElse { decodeModule(bytes) }

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

internal class MeasuredMethod(
    val node: MethodNode,
    val codeLength: Int,
)

internal fun measuredMethods(bytes: ByteArray): List<MeasuredMethod> {

    val reader = ClassReader(bytes)
    val owner = ClassNode()
    reader.accept(owner, 0)

    return owner.methods.filter { method -> method.instructions.size() > 0 }.map { method ->
        val end = LabelNode()
        method.instructions.add(end)
        val writer = ClassWriter(reader, 0)
        writer.visit(owner.version, owner.access, owner.name, owner.signature, owner.superName, owner.interfaces.toTypedArray())
        method.accept(writer)
        MeasuredMethod(method, end.label.offset)
    }
}

internal class Generated : JvmOutput {
    val classes = LinkedHashMap<String, ByteArray>()
    val resources = LinkedHashMap<String, ByteArray>()

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
}

internal class GeneratedClassLoader(
    private val generated: Generated,
) : ClassLoader(Generated::class.java.classLoader) {

    override fun findClass(name: String): Class<*> {
        val bytes = generated.classes[name.replace('.', '/')] ?: throw ClassNotFoundException(name)
        return defineClass(name, bytes, 0, bytes.size)
    }

    fun verifyEveryClass() {
        generated.classes.keys.forEach { name ->
            Class.forName(name.replace('/', '.'), true, this)
        }
    }
}
