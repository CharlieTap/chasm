package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.value.NumberValue.I64
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class LocalThrowsTest {

    private val tagsModule = dexModule("host_exceptions_tags")
    private val store = store(tagsModule)
    private val instance by lazy { instantiate(store) }

    @Test
    fun anInnerMissFallsThroughToTheOuterHandler() {
        assertEquals(I32(4), call("inner_miss_outer_match", I32(3)))
    }

    @Test
    fun catchAllContinuesAfterTheHandler() {
        assertEquals(I32(5), call("catch_all_then_continue", I32(3)))
    }

    @Test
    fun caughtReferencesRethrowToTheOuterHandler() {
        assertEquals(I32(6), call("rethrow_caught_reference", I32(3)))
    }

    @Test
    fun widePayloadsArriveIntact() {
        assertEquals(I64(14L), call("wide_payload", I64(10L)))
    }

    @Test
    fun catchRefDeliversThePayloadAndTheReference() {
        assertEquals(I32(8), call("catch_ref_payload_and_reference", I32(3)))
    }

    @Test
    fun importedAliasesOfOneTagMatch() {
        assertEquals(I32(9), call("imported_aliases_match", I32(3)))
    }

    @Test
    fun differentImportedTagsDoNotMatch() {
        assertEquals(I32(10), call("different_imported_tags_miss", I32(3)))
    }

    @Test
    fun importedClausesSkipDefinedThrows() {
        assertEquals(I32(11), call("imported_clause_skips_defined_throw", I32(3)))
    }

    @Test
    fun throwsInALoopAreEachCaught() {
        assertEquals(I32(THROWS), call("throws_in_a_loop", I32(THROWS)))
    }

    @Test
    fun uncaughtLocalThrowsFailTheCall() {

        val uncaught = invoke(instance, "uncaught_propagates", listOf(I32(3)))

        assertIs<ChasmResult.Error<*>>(uncaught)
        assertEquals(I32(5), call("catch_all_then_continue", I32(3)))
    }

    private fun call(
        name: String,
        argument: ExecutionValue,
    ): ExecutionValue {
        return invoke(instance, name, listOf(argument)).expect(name).single()
    }

    private fun instantiate(store: Store): Instance {

        val tags = runBlocking { instance(store, tagsModule) }.expect("host_exceptions_tags")
        val boom = checkNotNull(exports(tags).tag("boom"))
        val spare = checkNotNull(exports(tags).tag("spare"))

        return instance(store, "local_throws", listOf(Import("tags", "boom", boom), Import("tags", "boom", boom), Import("tags", "spare", spare)))
    }

    private fun instance(
        store: Store,
        name: String,
        imports: List<Import>,
    ): Instance {

        val module = dexModule(name)

        return runBlocking { instance(store, module, imports) }.expect(name)
    }

    private companion object {
        const val THROWS = 1_000
    }
}
