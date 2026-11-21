package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.compiled.Import
import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.exports
import io.github.charlietap.chasm.embedding.compiled.function
import io.github.charlietap.chasm.embedding.compiled.host.HostFunction
import io.github.charlietap.chasm.embedding.compiled.host.allocateStruct
import io.github.charlietap.chasm.embedding.compiled.host.createException
import io.github.charlietap.chasm.embedding.compiled.host.raise
import io.github.charlietap.chasm.embedding.compiled.host.readI32
import io.github.charlietap.chasm.embedding.compiled.host.readReference
import io.github.charlietap.chasm.embedding.compiled.host.readStructReference
import io.github.charlietap.chasm.embedding.compiled.host.runtimeType
import io.github.charlietap.chasm.embedding.compiled.host.withTag
import io.github.charlietap.chasm.embedding.compiled.host.writeI32
import io.github.charlietap.chasm.embedding.compiled.host.writeReference
import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.compiled.invoke
import io.github.charlietap.chasm.embedding.compiled.readStructField
import io.github.charlietap.chasm.embedding.compiled.readStructReference
import io.github.charlietap.chasm.embedding.compiled.readTable
import io.github.charlietap.chasm.embedding.compiled.value.ExecutionValue
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.compiled.writeGlobalReference
import io.github.charlietap.chasm.embedding.compiled.writeStructField
import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.reference.StructReference
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HostReferencesTest {

    @Test
    fun hostFunctionsReadAndWriteGuestStructs() {

        val (store, instance) = instantiate()

        val result = invoke(store, instance, "inspect", listOf(I32(3))).expect("inspect")

        assertEquals(listOf<ExecutionValue>(I32(3 + 200 + 7 + 1000)), result)
    }

    @Test
    fun hostAllocatedStructsCarryHostValues() {

        val (store, instance) = instantiate()

        val result = invoke(store, instance, "make", emptyList()).expect("make")

        assertEquals(listOf<ExecutionValue>(I32(5 + 1)), result)
    }

    @Test
    fun hostRaisedExceptionsAreCaughtByTheGuest() {

        val (store, instance) = instantiate()

        val result = invoke(store, instance, "catch_host", listOf(I32(20))).expect("catch_host")

        assertEquals(listOf<ExecutionValue>(I32(41)), result)
    }

    @Test
    fun guestStructsMoveThroughEmbeddingTablesAndGlobals() {

        val (store, instance) = instantiate()
        val exports = exports(instance)
        invoke(store, instance, "store", listOf(I32(9))).expect("store")

        val struct = assertIs<StructReference>(readTable(checkNotNull(exports.table("things")), 1))
        val field = readStructField(struct, 0)
        writeGlobalReference(checkNotNull(exports.global("saved")), struct)
        val saved = invoke(store, instance, "saved_x", emptyList()).expect("saved_x")

        assertEquals(9L, field)
        assertEquals(listOf<ExecutionValue>(I32(9)), saved)
    }

    private fun instantiate(): Pair<Store, Instance> {

        val store = store()
        val module = runBlocking { module(resourceBytes("modules/host_references.wasm"), validate = true) }.expect("module")
        val instance = runBlocking { instance(store, module, imports(store)) }.expect("instance")

        return store to instance
    }

    private fun imports(
        store: Store,
    ): List<Import> {
        return listOf(
            import(store, "inspect", inspect) {
                params { structref() }
                results { i32() }
            },
            import(store, "make", make) {
                results { structref() }
            },
            import(store, "raise", raise) {
                params { i32() }
            },
            import(store, "label", label) {
                params { externref() }
                results { i32() }
            },
        )
    }

    private fun import(
        store: Store,
        name: String,
        hostFunction: HostFunction,
        signature: FunctionTypeBuilder.() -> Unit,
    ): Import {
        return Import("host", name, function(store, FunctionTypeBuilder().apply(signature).build(), hostFunction))
    }

    private val inspect = HostFunction { parameters, results ->

        val point = parameters.readStructReference(0)
        val next = readStructReference(point, NEXT) as StructReference
        val sum = readStructField(point, X).toInt() + readStructField(point, TAG).toInt() + readStructField(next, X).toInt()
        writeStructField(point, X, 1000L)

        results.writeI32(0, sum)
    }

    private val make = HostFunction { _, results ->
        val point = allocateStruct(runtimeType(POINT), longArrayOf(5L, 0L, 0L, 0L), arrayOf<Any?>(null, null, null, LABEL))
        results.writeReference(0, point)
    }

    private val raise = HostFunction { parameters, _ ->
        val words = longArrayOf(parameters.readI32(0) * 2L, 0L)
        withTag(0) {
            raise(createException(words, arrayOfNulls(2)))
        }
    }

    private val label = HostFunction { parameters, results ->
        results.writeI32(0, if (parameters.readReference(0) == LABEL) 1 else 0)
    }

    private companion object {
        const val POINT = 0
        const val X = 0
        const val TAG = 1
        const val NEXT = 2
        const val LABEL = "hello"
    }
}
