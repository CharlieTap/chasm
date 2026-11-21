package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import io.github.charlietap.chasm.type.FunctionType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class FunctionReferenceTest {

    private val store = store()
    private val exporter by lazy { instantiate(store, "modules/function_exporter.wasm", emptyList()) }
    private val importer by lazy { instantiate(store, "modules/function_importer.wasm", imports(store, exporter)) }

    @Test
    fun theSameFunctionIsOneObjectAcrossModules() {

        val fromExporter = reference(exporter, "double_ref")
        val fromImporter = reference(importer, "double_ref")

        assertEquals(WasmReferenceKind.Function, fromExporter.kind)
        assertEquals(fromExporter, fromImporter)
        assertEquals(fromExporter, reference(exporter, "double_ref"))
        assertNotEquals(fromExporter, reference(importer, "quad_ref"))
    }

    @Test
    fun callRefReachesCompiledImportedAndHostFunctions() {

        assertEquals(10, callRef(reference(exporter, "double_ref"), 5))
        assertEquals(15, callRef(reference(importer, "triple_ref"), 5))
        assertEquals(20, callRef(reference(importer, "quad_ref"), 5))
    }

    @Test
    fun callIndirectReachesFunctionsFromAnElementSegment() {

        prepare(importer, "init").call(LongArray(0), ReferenceSlots(0)).expect("init")

        assertEquals(14, callIndirect(0, 7))
        assertEquals(21, callIndirect(1, 7))
        assertEquals(28, callIndirect(2, 7))
    }

    @Test
    fun functionReferencesAnswerTypeTests() {

        val double = reference(exporter, "double_ref")
        val triple = reference(importer, "triple_ref")

        assertEquals(1, test("is_sig", double))
        assertEquals(1, test("is_sig", triple))
        assertEquals(0, test("is_other", double))
        assertEquals(1, test("is_func", double))
    }

    @Test
    fun functionReferencesFromAnotherStoreAreRejected() {

        val otherStore = store()
        val otherExporter = instantiate(otherStore, "modules/function_exporter.wasm", emptyList())
        val foreignCall = prepareCall(otherStore, otherExporter, "double_ref").expect("double_ref")
        val foreignReferences = ReferenceSlots(foreignCall.slots)
        foreignCall.call(LongArray(foreignCall.slots), foreignReferences).expect("double_ref")
        val foreign = foreignReferences[0]

        val call = prepare(importer, "call_ref")
        val references = ReferenceSlots(call.slots)
        references[0] = foreign

        val result = call.call(LongArray(call.slots), references)

        assertIs<ChasmResult.Error<*>>(result)
    }

    @Test
    fun functionReferencesMatchTheirDeclaredSupertype() {

        val five = reference(importer, "five_ref")

        assertEquals(1, test("is_base", five))
        assertEquals(1, test("is_derived", five))
        assertEquals(0, test("is_base", reference(importer, "quad_ref")))
    }

    @Test
    fun functionReferencesSurviveGlobalsAndTables() {

        val double = reference(exporter, "double_ref")

        pass("save", double)
        pass("hold", double)

        assertEquals(double, reference(importer, "saved"))
        assertEquals(double, reference(importer, "held"))
    }

    @Test
    fun functionReferencesAreRejectedAsExternReferences() {

        val call = prepare(importer, "extern_identity")
        val references = ReferenceSlots(call.slots)
        references[0] = reference(exporter, "double_ref")

        val result = call.call(LongArray(call.slots), references)

        assertIs<ChasmResult.Error<*>>(result)
    }

    @Test
    fun trapsThroughCallRefNameTheFunction() {

        val debugStore = store()
        val debugExporter = instantiate(debugStore, "modules/function_exporter.wasm", emptyList())
        val debugImporter = instantiate(debugStore, "modules/function_importer.wasm", imports(debugStore, debugExporter), RuntimeConfig(debugInfo = true))
        val result = invoke(debugStore, debugImporter, "call_boom", listOf(I32(1)))

        val error = assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(result).error
        val frames = checkNotNull(error.trap).frames
        assertTrue(frames.any { frame -> frame.functionName == "boom" }, frames.toString())
    }

    private fun reference(
        instance: Instance,
        name: String,
    ): WasmReference {

        val call = prepare(instance, name)
        val references = ReferenceSlots(call.slots)

        call.call(LongArray(call.slots), references).expect(name)

        return references[0]
    }

    private fun pass(
        name: String,
        function: WasmReference,
    ) {

        val call = prepare(importer, name)
        val references = ReferenceSlots(call.slots)
        references[0] = function

        call.call(LongArray(call.slots), references).expect(name)
    }

    private fun callRef(
        function: WasmReference,
        argument: Int,
    ): Int {

        val call = prepare(importer, "call_ref")
        val stack = LongArray(call.slots)
        val references = ReferenceSlots(call.slots)
        references[0] = function
        stack[1] = argument.toLong()

        call.call(stack, references).expect("call_ref")

        return stack[0].toInt()
    }

    private fun callIndirect(
        index: Int,
        argument: Int,
    ): Int {

        val call = prepare(importer, "call_indirect")
        val stack = longArrayOf(index.toLong(), argument.toLong())

        call.call(stack, ReferenceSlots(call.slots)).expect("call_indirect")

        return stack[0].toInt()
    }

    private fun test(
        name: String,
        function: WasmReference,
    ): Int {

        val call = prepare(importer, name)
        val stack = LongArray(call.slots)
        val references = ReferenceSlots(call.slots)
        references[0] = function

        call.call(stack, references).expect(name)

        return stack[0].toInt()
    }

    private fun prepare(
        instance: Instance,
        name: String,
    ): PreparedCall {
        return prepareCall(store, instance, name).expect(name)
    }

    private fun instantiate(
        store: Store,
        resource: String,
        imports: List<Import>,
        config: RuntimeConfig = RuntimeConfig(),
    ): Instance {

        val module = runBlocking { module(resourceBytes(resource), validate = true) }.expect("module")

        return runBlocking { instance(store, module, imports, config) }.expect("instance")
    }

    private fun imports(
        store: Store,
        exporter: Instance,
    ): List<Import> {

        val double = exports(exporter).first { export -> export.name == "double" }.value
        val signature = functionType {
            params { i32() }
            results { i32() }
        }

        return listOf(
            Import("a", "double", double),
            Import("host", "triple", referenceFunction(store, signature, triple)),
        )
    }

    private val triple = ReferenceHostFunction { parameters, results ->
        results.writeI32(0, parameters.readI32(0) * 3)
    }
}

private fun functionType(
    builder: FunctionTypeBuilder.() -> Unit,
): FunctionType {
    return FunctionTypeBuilder().apply(builder).build()
}
