@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.dex

import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedClassDef
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.ArrayPayload
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.compiled.format.ArtifactTarget
import io.github.charlietap.chasm.compiled.format.UNPINNED_OFFSET
import io.github.charlietap.chasm.compiler.backend.abi.COMPILED_MODULE
import io.github.charlietap.chasm.compiler.backend.abi.ModuleSetting
import io.github.charlietap.chasm.compiler.backend.abi.ModuleSettings
import io.github.charlietap.chasm.compiler.backend.abi.binaryName
import io.github.charlietap.chasm.compiler.backend.analysis.gc.STORAGE_FAMILY
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StoragePlans
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StructLanes
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.Bytes
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.Fuel
import io.github.charlietap.chasm.config.GrowthFailureStrategy
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModuleClassTest {

    @Test
    fun `the entry class implements CompiledModule`() {

        val entry = classes(generate(decodeModule(fixture("gc")), OPTIONS).dex).getValue(ENTRY)

        assertEquals(listOf(COMPILED_MODULE), entry.interfaces)
        assertEquals(listOf("COMPILED"), entry.staticFields.map { field -> field.name })
        assertEquals(ENTRY, entry.staticFields.single().type)
    }

    @Test
    fun `settings read back`() {

        val entry = classes(generate(decodeModule(fixture("memory")), OPTIONS).dex).getValue(ENTRY)
        val expected = ModuleSettings(OPTIONS.runtime, ArtifactTarget.ART, BYTE_ARRAY_BASE).constants()

        expected.forEach { setting ->
            val literal = literal(entry.method(setting.name))
            val value = when (setting) {
                is ModuleSetting.IntSetting -> setting.value.toLong()
                is ModuleSetting.LongSetting -> setting.value
                is ModuleSetting.BooleanSetting -> if (setting.value) 1L else 0L
            }
            assertEquals(value, literal, setting.name)
        }
        assertEquals(1L, literal(entry.method("initializesEagerly")))
        assertEquals(listOf("chasm/dex/WasmModule"), strings(entry.method("resourceRoot")))
    }

    @Test
    fun `a module that never reads memory through the byte array base leaves it unpinned`() {

        val entry = classes(generate(decodeModule(UNREAD_MEMORY), OPTIONS).dex).getValue(ENTRY)

        assertEquals(UNPINNED_OFFSET, literal(entry.method("byteArrayBaseOffset")))
    }

    @Test
    fun `the class inventory lists every generated class but the entry, signature interfaces included`() {

        val generated = generate(decodeModule(fixture("calls")), OPTIONS)
        val classes = classes(generated.dex)
        val entry = classes.getValue(ENTRY)
        val inventory = entry.methods.filter { method -> method.name.startsWith("classes") && method.name != "classes" }
            .sortedBy { method -> method.name.removePrefix("classes").toInt() }
            .flatMap(::strings)

        assertEquals(classes.keys.filter { type -> type != ENTRY }.map(::binaryName).sorted(), inventory.sorted())
        assertTrue(inventory.any { name -> name.contains("\$Sig") })
    }

    @Test
    fun `struct lanes match the storage plans`() {

        val module = decodeModule(fixture("gc"))
        val entry = classes(generate(module, OPTIONS).dex).getValue(ENTRY)
        val payload = entry.method("structLanes").implementation!!.instructions.filterIsInstance<ArrayPayload>().single()
        val expected = StructLanes(StoragePlans(module.definedTypes, STORAGE_FAMILY), module.definedTypes).encode()

        assertTrue(expected.isNotEmpty())
        assertContentEquals(expected, payload.arrayElements.map(Number::toLong).toLongArray())
    }

    @Test
    fun `declarations are split across declare classes`() {

        val classes = classes(generate(decodeModule(globalsModule(GLOBALS)), OPTIONS).dex)
        val declares = classes.keys.filter { type -> type.contains("\$Declare") }.sorted()
        val called = classes.getValue(ENTRY).method("buildDeclarations").implementation!!.instructions
            .mapNotNull { instruction -> ((instruction as? ReferenceInstruction)?.reference as? MethodReference) }
            .filter { method -> method.name == "declare" }
            .map(MethodReference::getDefiningClass)

        assertTrue(declares.size > 1, "declares: $declares")
        assertEquals(declares, called)
    }

    @Test
    fun `metadata spans several files under tiny pool limits`() {

        val module = decodeModule(fixture("gc"))
        val code = ModuleCompiler(module).getOrThrow { failure -> IllegalStateException(failure.toString()) }
        val generated = lowerWithSeparateMetadata(
            module = module,
            code = code,
            options = OPTIONS,
            declarations = ModuleDeclarationsFactory(module),
            resources = CollectedResources(),
            limits = TINY,
        )
        val metadata = generated.dex.filter { bytes ->
            classes(listOf(bytes)).keys.none { type -> type.contains("\$Code") || type.contains("\$Entry") }
        }

        val uses = metadata.map { bytes ->
            PoolUse(
                strings = headerInt(bytes, STRING_IDS_SIZE),
                types = headerInt(bytes, TYPE_IDS_SIZE),
                fields = headerInt(bytes, FIELD_IDS_SIZE),
                methods = headerInt(bytes, METHOD_IDS_SIZE),
            )
        }
        val described = uses.joinToString { use -> "${use.strings}/${use.types}/${use.fields}/${use.methods}" }

        assertTrue(metadata.size > 1, "metadata files (strings/types/fields/methods): $described")
        assertTrue(uses.all { use -> use.within(TINY) }, "metadata files (strings/types/fields/methods): $described")
        assertEquals(generate(module, OPTIONS).classes.sorted(), generated.classes.sorted())
    }

    private fun classes(dex: List<ByteArray>): Map<String, DexBackedClassDef> {
        return dex.flatMap { bytes -> DexBackedDexFile(Opcodes.forApi(MIN_API), bytes).classes }.associateBy { classDef -> classDef.type }
    }

    private fun DexBackedClassDef.method(name: String): Method {
        return methods.single { method -> method.name == name }
    }

    private fun literal(method: Method): Long {
        return method.implementation!!.instructions.filterIsInstance<WideLiteralInstruction>().single().wideLiteral
    }

    private fun strings(method: Method): List<String> {
        return method.implementation!!.instructions
            .mapNotNull { instruction -> ((instruction as? ReferenceInstruction)?.reference as? StringReference)?.string }
    }

    private fun headerInt(
        bytes: ByteArray,
        offset: Int,
    ): Int {
        return (bytes[offset].toInt() and 0xFF) or
            ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
            ((bytes[offset + 2].toInt() and 0xFF) shl 16) or
            ((bytes[offset + 3].toInt() and 0xFF) shl 24)
    }

    private fun globalsModule(count: Int): ByteArray {

        val globals = ArrayList<Byte>()
        globals.leb(count)
        repeat(count) {
            globals.bytes(I32, IMMUTABLE, I32_CONST, 0, END)
        }

        val module = ArrayList<Byte>()
        module.bytes(0x00, 0x61, 0x73, 0x6D, 0x01, 0x00, 0x00, 0x00)
        module.bytes(GLOBAL_SECTION)
        module.leb(globals.size)
        module.addAll(globals)

        return module.toByteArray()
    }

    private fun ArrayList<Byte>.bytes(vararg values: Int) {
        values.forEach { value -> add(value.toByte()) }
    }

    private fun ArrayList<Byte>.leb(value: Int) {

        var remaining = value
        do {
            val byte = remaining and 0x7F
            remaining = remaining ushr 7
            add((if (remaining != 0) byte or 0x80 else byte).toByte())
        } while (remaining != 0)
    }

    private companion object {
        const val ENTRY = "Lchasm/dex/WasmModule;"
        const val BYTE_ARRAY_BASE = 16L
        const val GLOBALS = 400
        const val GLOBAL_SECTION = 6
        const val I32 = 0x7F
        const val IMMUTABLE = 0x00
        const val I32_CONST = 0x41
        const val END = 0x0B
        const val STRING_IDS_SIZE = 0x38
        const val TYPE_IDS_SIZE = 0x40
        const val FIELD_IDS_SIZE = 0x50
        const val METHOD_IDS_SIZE = 0x58

        val OPTIONS = DexOptions(
            runtime = RuntimeConfig(
                interrupts = true,
                stacktraces = true,
                limits = Limits(
                    cpu = CpuLimits.Metered(Fuel(1_000)),
                    memory = MemoryLimits(
                        linearMemory = Bytes(1_048_576),
                        gcHeap = Bytes(2_097_152),
                        tables = Bytes(4_096),
                        stack = Bytes(8_192),
                        onGrowthFailure = GrowthFailureStrategy.Trap,
                    ),
                ),
            ),
            byteArrayBaseOffset = BYTE_ARRAY_BASE,
        )

        val TINY = PoolLimits(strings = 72, types = 32, fields = 32, methods = 32)

        val UNREAD_MEMORY = byteArrayOf(0x00, 0x61, 0x73, 0x6D, 0x01, 0x00, 0x00, 0x00, 0x05, 0x03, 0x01, 0x00, 0x01)
    }
}
