package io.github.charlietap.chasm.compiler.backend.jvm.declarations

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.backend.jvm.Generated
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.declarations.DataPayload
import io.github.charlietap.chasm.declarations.DataReader
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.declarations.construction.DeclarationsBuilder
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.WasmModuleValidator
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.IdentityHashMap
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class DeclarationCallsTest {

    @Test
    fun `every fixture's declarations come back the same through the builder`() {
        fixtures().forEach { file -> roundTrip(file.name, file.readBytes()) }
    }

    @Test
    fun `every fixture's declarations come back the same through the generated classes`() {
        fixtures().forEach { file ->
            val expected = ModuleDeclarationsFactory(decode(file.name, file.readBytes()))
            val payloads = ByteArrayOutputStream()
            val calls = DeclarationCallGenerator(expected, payloads)
            val data = payloads.toByteArray()
            val output = Generated()
            val roots = DeclarationsLowerer(calls, "chasm/test", output)
            val loader = GeneratedClasses(output)
            val builder = DeclarationsBuilder()
            roots.forEach { root ->
                loader.loadClass(root.replace('/', '.'))
                    .getDeclaredMethod(DECLARE, DeclarationsBuilder::class.java)
                    .apply { isAccessible = true }
                    .invoke(null, builder)
            }
            val actual = builder.build { dataReader(data) }

            try {
                assertSameDeclarations(expected, actual, data)
            } catch (failure: AssertionError) {
                throw AssertionError("${file.name}: ${failure.message}", failure)
            }
        }
    }

    @Test
    fun `every corpus module's declarations come back the same through the builder`() {
        if (System.getProperty("codegen.census") != "true") return
        val root = File(System.getProperty("user.dir"), "../../chasm/build/wasm-corpus-resources/corpus")
        val modules = root.walkTopDown().filter { it.extension == "wasm" }.sortedBy { it.path }.toList()
        assertTrue(modules.isNotEmpty(), "no corpus modules under $root")
        modules.forEach { file -> roundTrip(file.relativeTo(root).path, file.readBytes()) }
        println("DECLARATIONS round trip modules=${modules.size}")
    }

    private fun fixtures(): List<File> {
        val fixtures = File(System.getProperty("user.dir"), "src/test/resources/fixtures")
            .walkTopDown().filter { it.extension == "wasm" }.sortedBy { it.path }.toList()
        assertTrue(fixtures.isNotEmpty(), "no fixtures")
        return fixtures
    }

    private fun decode(
        name: String,
        bytes: ByteArray,
    ): Module {
        val config = ModuleConfig(decodeNameSection = true)
        val decoded = WasmModuleDecoder(config, bytes).getOrThrow { IllegalStateException("$name: $it") }
        return WasmModuleValidator(config, decoded).getOrThrow { IllegalStateException("$name: $it") }
    }

    private fun roundTrip(
        name: String,
        bytes: ByteArray,
    ) {
        val expected = ModuleDeclarationsFactory(decode(name, bytes))

        val payloads = ByteArrayOutputStream()
        val calls = DeclarationCallGenerator(expected, payloads)
        val data = payloads.toByteArray()
        val actual = replay(calls, data)

        try {
            assertSameDeclarations(expected, actual, data)
        } catch (failure: AssertionError) {
            throw AssertionError("$name: ${failure.message}", failure)
        }
    }

    private fun replay(
        calls: List<BuilderCall>,
        data: ByteArray,
    ): ModuleDeclarations {
        val builder = DeclarationsBuilder()
        val methods = DeclarationsBuilder::class.java.methods.groupBy { it.name }
        calls.forEach { call ->
            val method = methods[call.methodName]?.singleOrNull { it.parameterCount == call.arguments.size }
                ?: fail("no builder method ${call.methodName}/${call.arguments.size}")
            method.invoke(builder, *call.arguments.toTypedArray())
        }
        return builder.build { dataReader(data) }
    }

    private fun dataReader(
        data: ByteArray,
    ): DataReader = object : DataReader {
        override fun read(
            offset: Long,
            size: Int,
        ): ByteArray = data.copyOfRange(offset.toInt(), offset.toInt() + size)

        override fun close() = Unit
    }

    private class GeneratedClasses(
        private val output: Generated,
    ) : ClassLoader(DeclarationsBuilder::class.java.classLoader) {
        override fun findClass(
            name: String,
        ): Class<*> {
            val bytes = output.classes[name.replace('.', '/')] ?: throw ClassNotFoundException(name)
            return defineClass(name, bytes, 0, bytes.size)
        }
    }
}

internal fun assertSameDeclarations(
    expected: ModuleDeclarations,
    actual: ModuleDeclarations,
    actualPayloads: ByteArray,
) {
    assertSameTypes(expected.definedTypes, actual.definedTypes)
    assertSameList(expected.imports, actual.imports, "import")
    assertContentEquals(expected.functionTypeIndices, actual.functionTypeIndices, "function types")
    assertSameList(expected.tables, actual.tables, "table")
    assertSameList(expected.memories, actual.memories, "memory")
    assertSameList(expected.tags, actual.tags, "tag")
    assertSameList(expected.globals, actual.globals, "global")
    assertEquals(expected.exports, actual.exports, "exports")
    assertEquals(expected.startFunction, actual.startFunction, "start")
    assertSameList(expected.elementSegments, actual.elementSegments, "element segment")
    assertEquals(expected.dataSegments.size, actual.dataSegments.size, "data segments")
    expected.dataSegments.zip(actual.dataSegments).forEachIndexed { index, (expectedSegment, actualSegment) ->
        assertEquals(expectedSegment.mode, actualSegment.mode, "data segment $index mode")
        val expectedBytes = (expectedSegment.payload as DataPayload.Inline).bytes
        val actualBytes = when (val payload = actualSegment.payload) {
            is DataPayload.Inline -> payload.bytes
            is DataPayload.External -> actualPayloads.copyOfRange(payload.offset.toInt(), payload.offset.toInt() + payload.size)
        }
        assertContentEquals(expectedBytes, actualBytes, "data segment $index bytes")
    }
    assertEquals(expected.names, actual.names, "names")
}

private fun <T> assertSameList(
    expected: List<T>,
    actual: List<T>,
    item: String,
) {
    assertEquals(expected.size, actual.size, "${item}s")
    expected.zip(actual).forEachIndexed { index, (e, a) -> assertEquals(e, a, "$item $index") }
}

private fun assertSameTypes(
    expected: List<DefinedType>,
    actual: List<DefinedType>,
) {
    assertEquals(expected.size, actual.size, "type count")
    val expectedIndices = IdentityHashMap<DefinedType, Int>().apply { expected.forEachIndexed { i, t -> put(t, i) } }
    val actualIndices = IdentityHashMap<DefinedType, Int>().apply { actual.forEachIndexed { i, t -> put(t, i) } }
    val groups = IdentityHashMap<RecursiveType, RecursiveType>()
    val reverse = IdentityHashMap<RecursiveType, RecursiveType>()
    expected.indices.forEach { index ->
        val e = expected[index]
        val a = actual[index]
        assertEquals(e.typeIndex, a.typeIndex, "type $index index")
        assertEquals(e.recursiveTypeIndex, a.recursiveTypeIndex, "type $index member")
        val mapped = groups.getOrPut(e.recursiveType) { a.recursiveType }
        assertTrue(mapped === a.recursiveType, "type $index group identity")
        val mappedBack = reverse.getOrPut(a.recursiveType) { e.recursiveType }
        assertTrue(
            mappedBack === e.recursiveType,
            "type $index shares a group with another type it should not: members ${e.recursiveType.subTypes.size}, " +
                "member ${e.recursiveTypeIndex}, previous type in group ${expected.indexOfFirst { it.recursiveType === mappedBack }}",
        )
        assertEquals(e.recursiveType.state, a.recursiveType.state, "type $index group state")
    }
    groups.forEach { (e, a) ->
        assertEquals(e.subTypes.size, a.subTypes.size, "group members")
        e.subTypes.zip(a.subTypes).forEach { (es, at) -> sameSubType(es, at, expectedIndices, actualIndices) }
    }
}

private fun sameSubType(
    e: SubType,
    a: SubType,
    ei: Map<DefinedType, Int>,
    ai: Map<DefinedType, Int>,
) {
    assertEquals(e::class, a::class, "subtype finality")
    assertEquals(e.superTypes.size, a.superTypes.size, "supertypes")
    e.superTypes.zip(a.superTypes).forEach { (es, at) -> sameHeap(es, at, ei, ai) }
    when (val ec = e.compositeType) {
        is CompositeType.Function -> {
            val ac = a.compositeType as CompositeType.Function
            sameValues(ec.functionType.params.types, ac.functionType.params.types, ei, ai)
            sameValues(ec.functionType.results.types, ac.functionType.results.types, ei, ai)
        }
        is CompositeType.Struct -> {
            val ac = a.compositeType as CompositeType.Struct
            assertEquals(ec.structType.fields.size, ac.structType.fields.size, "struct fields")
            ec.structType.fields.zip(ac.structType.fields).forEach { (ef, af) -> sameField(ef, af, ei, ai) }
        }
        is CompositeType.Array -> sameField(ec.arrayType.fieldType, (a.compositeType as CompositeType.Array).arrayType.fieldType, ei, ai)
    }
}

private fun sameField(
    e: FieldType,
    a: FieldType,
    ei: Map<DefinedType, Int>,
    ai: Map<DefinedType, Int>,
) {
    assertEquals(e.mutability, a.mutability, "field mutability")
    when (val es = e.storageType) {
        is StorageType.Packed -> assertEquals(es, a.storageType, "field storage")
        is StorageType.Value -> sameValue(es.type, (a.storageType as StorageType.Value).type, ei, ai)
    }
}

private fun sameValues(
    e: List<ValueType>,
    a: List<ValueType>,
    ei: Map<DefinedType, Int>,
    ai: Map<DefinedType, Int>,
) {
    assertEquals(e.size, a.size, "value types")
    e.zip(a).forEach { (ev, av) -> sameValue(ev, av, ei, ai) }
}

private fun sameValue(
    e: ValueType,
    a: ValueType,
    ei: Map<DefinedType, Int>,
    ai: Map<DefinedType, Int>,
) {
    if (e is ValueType.Reference) {
        val ar = (a as ValueType.Reference).referenceType
        assertEquals(e.referenceType::class, ar::class, "nullability")
        sameHeap(e.referenceType.heapType, ar.heapType, ei, ai)
    } else {
        assertEquals(e, a, "value type")
    }
}

private fun sameHeap(
    e: HeapType,
    a: HeapType,
    ei: Map<DefinedType, Int>,
    ai: Map<DefinedType, Int>,
) {
    if (e is ConcreteHeapType.Defined) {
        val ad = (a as ConcreteHeapType.Defined).definedType
        assertEquals(ei[e.definedType] ?: e.definedType.typeIndex, ai[ad], "defined heap type")
    } else {
        assertEquals(e, a, "heap type")
    }
}
