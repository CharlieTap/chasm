package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.global.writeGlobalReference
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.reference.readStructField
import io.github.charlietap.chasm.embedding.reference.readStructReference
import io.github.charlietap.chasm.embedding.reference.writeStructField
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.table.readTable
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.allocateStruct
import io.github.charlietap.chasm.host.createException
import io.github.charlietap.chasm.host.raise
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.readReference
import io.github.charlietap.chasm.host.readStructReference
import io.github.charlietap.chasm.host.runtimeType
import io.github.charlietap.chasm.host.withTag
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.host.writeReference
import io.github.charlietap.chasm.reference.StructReference
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HostReferencesTest {

    @Test
    fun hostFunctionsReadAndWriteGuestStructs() {

        val instance = instantiate()

        val result = invoke(instance, "inspect", listOf(I32(3))).expect("inspect")

        assertEquals(listOf<ExecutionValue>(I32(3 + 200 + 7 + 1000)), result)
    }

    @Test
    fun hostAllocatedStructsCarryHostValues() {

        val instance = instantiate()

        val result = invoke(instance, "make", emptyList()).expect("make")

        assertEquals(listOf<ExecutionValue>(I32(5 + 1)), result)
    }

    @Test
    fun hostRaisedExceptionsAreCaughtByTheGuest() {

        val instance = instantiate()

        val result = invoke(instance, "catch_host", listOf(I32(20))).expect("catch_host")

        assertEquals(listOf<ExecutionValue>(I32(41)), result)
    }

    @Test
    fun guestStructsMoveThroughEmbeddingTablesAndGlobals() {

        val instance = instantiate()
        val exports = exports(instance)
        invoke(instance, "store", listOf(I32(9))).expect("store")

        val struct = assertIs<StructReference>(readTable(checkNotNull(exports.table("things")), 1))
        val field = readStructField(struct, 0)
        writeGlobalReference(checkNotNull(exports.global("saved")), struct)
        val saved = invoke(instance, "saved_x", emptyList()).expect("saved_x")

        assertEquals(9L, field)
        assertEquals(listOf<ExecutionValue>(I32(9)), saved)
    }

    private fun instantiate(): Instance {

        val store = store()
        val module = runBlocking { module(resourceBytes("modules/host_references.wasm"), validate = true) }.expect("module")

        return runBlocking { instance(store, module, imports(store)) }.expect("instance")
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
