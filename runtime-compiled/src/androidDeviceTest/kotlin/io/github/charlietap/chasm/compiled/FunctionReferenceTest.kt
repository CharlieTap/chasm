package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.invokeUnboxed
import io.github.charlietap.chasm.embedding.prepareFunction
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.PreparedFunction
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.type.FunctionType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class FunctionReferenceTest {

    private val exporterModule = dexModule("function_exporter")
    private val store = store(exporterModule)
    private val exporter by lazy { instantiate(store, exporterModule, emptyList()) }
    private val importer by lazy { instantiate(store, dexModule("function_importer"), imports(store, exporter)) }

    @Test
    fun theSameFunctionIsOneObjectAcrossModules() {

        val fromExporter = reference(exporter, "double_ref")
        val fromImporter = reference(importer, "double_ref")

        assertIs<FunctionReference>(fromExporter)
        assertSame(fromExporter, fromImporter)
        assertSame(fromExporter, reference(exporter, "double_ref"))
        assertNotSame(fromExporter, reference(importer, "quad_ref"))
    }

    @Test
    fun callRefReachesCompiledImportedAndHostFunctions() {

        assertEquals(10, callRef(reference(exporter, "double_ref"), 5))
        assertEquals(15, callRef(reference(importer, "triple_ref"), 5))
        assertEquals(20, callRef(reference(importer, "quad_ref"), 5))
    }

    @Test
    fun callIndirectReachesFunctionsFromAnElementSegment() {

        prepare(importer, "init").invokeUnboxed(LongArray(0), arrayOfNulls(0)).expect("init")

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

        val otherStore = store(exporterModule)
        val otherExporter = instantiate(otherStore, exporterModule, emptyList())
        val foreignCall = prepareFunction(otherExporter, "double_ref").expect("double_ref")
        val foreignReferences = arrayOfNulls<Any>(foreignCall.slots)
        foreignCall.invokeUnboxed(LongArray(foreignCall.slots), foreignReferences).expect("double_ref")
        val foreign = foreignReferences[0]

        val call = prepare(importer, "call_ref")
        val references = arrayOfNulls<Any>(call.slots)
        references[0] = foreign

        val result = call.invokeUnboxed(LongArray(call.slots), references)

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

        assertSame(double, reference(importer, "saved"))
        assertSame(double, reference(importer, "held"))
    }

    @Test
    fun functionReferencesAreRejectedAsExternReferences() {

        val call = prepare(importer, "extern_identity")
        val references = arrayOfNulls<Any>(call.slots)
        references[0] = reference(exporter, "double_ref")

        val result = call.invokeUnboxed(LongArray(call.slots), references)

        assertIs<ChasmResult.Error<*>>(result)
    }

    @Test
    fun trapsThroughCallRefNameTheFunction() {

        val debug = RuntimeConfig(stacktraces = true)
        val debugExporterModule = dexModule("function_exporter", debug)
        val debugStore = store(debugExporterModule)
        val debugExporter = instantiate(debugStore, debugExporterModule, emptyList())
        val debugImporter = instantiate(debugStore, dexModule("function_importer", debug), imports(debugStore, debugExporter))
        val result = invoke(debugImporter, "call_boom", listOf(I32(1)))

        val error = assertIs<ChasmResult.Error<*>>(result).error
        val frames = checkNotNull(assertIs<ExecutionError.Trapped>(error).trap.stackTrace).frames
        assertTrue(frames.any { frame -> frame.functionName == "boom" }, frames.toString())
    }

    private fun reference(
        instance: Instance,
        name: String,
    ): Any? {

        val call = prepare(instance, name)
        val references = arrayOfNulls<Any>(call.slots)

        call.invokeUnboxed(LongArray(call.slots), references).expect(name)

        return references[0]
    }

    private fun pass(
        name: String,
        function: Any?,
    ) {

        val call = prepare(importer, name)
        val references = arrayOfNulls<Any>(call.slots)
        references[0] = function

        call.invokeUnboxed(LongArray(call.slots), references).expect(name)
    }

    private fun callRef(
        function: Any?,
        argument: Int,
    ): Int {

        val call = prepare(importer, "call_ref")
        val words = LongArray(call.slots)
        val references = arrayOfNulls<Any>(call.slots)
        references[0] = function
        words[1] = argument.toLong()

        call.invokeUnboxed(words, references).expect("call_ref")

        return words[0].toInt()
    }

    private fun callIndirect(
        index: Int,
        argument: Int,
    ): Int {

        val call = prepare(importer, "call_indirect")
        val words = longArrayOf(index.toLong(), argument.toLong())

        call.invokeUnboxed(words, arrayOfNulls(call.slots)).expect("call_indirect")

        return words[0].toInt()
    }

    private fun test(
        name: String,
        function: Any?,
    ): Int {

        val call = prepare(importer, name)
        val words = LongArray(call.slots)
        val references = arrayOfNulls<Any>(call.slots)
        references[0] = function

        call.invokeUnboxed(words, references).expect(name)

        return words[0].toInt()
    }

    private fun prepare(
        instance: Instance,
        name: String,
    ): PreparedFunction {
        return prepareFunction(instance, name).expect(name)
    }

    private fun instantiate(
        store: Store,
        module: Module,
        imports: List<Import>,
    ): Instance {
        return runBlocking { instance(store, module, imports) }.expect("instance")
    }

    private fun imports(
        store: Store,
        exporter: Instance,
    ): List<Import> {

        val double = checkNotNull(exports(exporter).function("double"))
        val signature = functionType {
            params { i32() }
            results { i32() }
        }

        return listOf(
            Import("a", "double", double),
            Import("host", "triple", function(store, signature, triple)),
        )
    }

    private val triple = HostFunction { parameters, results ->
        results.writeI32(0, parameters.readI32(0) * 3)
    }
}

private fun functionType(
    builder: FunctionTypeBuilder.() -> Unit,
): FunctionType {
    return FunctionTypeBuilder().apply(builder).build()
}
