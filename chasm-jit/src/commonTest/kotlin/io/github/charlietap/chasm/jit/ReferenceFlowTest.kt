package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ReferenceFlowTest {

    private val store = store()
    private val instance = instantiate(store)

    @Test
    fun covariantFieldReadsKeepTheNarrowerType() {
        assertEquals(16, call("covariant_read"))
    }

    @Test
    fun nullOnlyDefinitionsFlowIntoConcreteFields() {
        assertEquals(7, call("null_only_definitions"))
    }

    @Test
    fun nullOnlyCastBranchesFlowIntoConcreteFields() {
        assertEquals(1, call("null_only_branch"))
    }

    @Test
    fun nullOnlyExceptionPayloadsFlowIntoConcreteFields() {
        assertEquals(1, call("null_only_payload"))
    }

    @Test
    fun castBranchesTakeTheMatchingEdge() {
        assertEquals(424, call("cast_branches"))
    }

    @Test
    fun siblingCastsSelectTheTestedType() {

        val left = invoke(instance, "make_left").expect("make_left").single()
        val right = invoke(instance, "make_right").expect("make_right").single()

        assertEquals(33, call("sibling_casts", left))
        assertEquals(-1, call("sibling_casts", right))
    }

    @Test
    fun joinsOfSubtypesReadSupertypeFields() {
        assertEquals(40, call("join", I32(1)))
        assertEquals(50, call("join", I32(0)))
        assertEquals(0, call("join", I32(2)))
    }

    @Test
    fun loopCarriedReferencesWalkALinkedList() {
        assertEquals(45, call("loop_variable"))
    }

    @Test
    fun structsPassThroughParametersAndResults() {
        assertEquals(13, call("pass_through"))
        assertEquals(432, call("multi_values"))
    }

    @Test
    fun tailCallsCarryStructs() {
        assertEquals(1005, call("tail_references"))
    }

    @Test
    fun exceptionPayloadsCarryStructs() {
        assertEquals(60, call("exception_payload"))
    }

    @Test
    fun globalsTablesAndIndirectResultsCarryStructs() {
        assertEquals(81, call("globals_and_tables"))
    }

    @Test
    fun covariantArrayReadsKeepTheNarrowerType() {
        assertEquals(81, call("covariant_array"))
    }

    @Test
    fun convertedReferencesCastBackToTheirType() {
        assertEquals(90, call("conversions"))
    }

    @Test
    fun i31CastBranchesSelectEqReferences() {
        assertEquals(1000, call("i31_paths", I32(1000)))
        assertEquals(-1, call("i31_paths", I32(-3)))
    }

    private fun call(
        name: String,
        vararg arguments: ExecutionValue,
    ): Int {
        val result = invoke(instance, name, arguments.toList()).expect(name).single()
        return (result as I32).value
    }

    private fun instantiate(store: Store): Instance {

        val module = runBlocking { module(resourceBytes("modules/reference_flow.wasm"), validate = true) }.expect("module")

        return runBlocking { instance(store, module, emptyList()) }.expect("instance")
    }
}
