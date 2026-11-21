package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.dsl.functionImport
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.global.writeGlobal
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.reference.readStructField
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.embedding.table.readTable
import io.github.charlietap.chasm.host.HostExternReference
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.readRawReference
import io.github.charlietap.chasm.host.withExceptions
import io.github.charlietap.chasm.host.withExterns
import io.github.charlietap.chasm.host.withGc
import io.github.charlietap.chasm.host.withTag
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.host.writeRawReference
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.FieldValue
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

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
        val exports = exports(instance).associate { export -> export.name to export.value }
        invoke(store, instance, "store", listOf(I32(9))).expect("store")

        val struct = readTable(store, exports.getValue("things") as Table, 1).expect("readTable") as ReferenceValue.Struct
        val field = readStructField(store, struct, 0).expect("readStructField")
        writeGlobal(store, exports.getValue("saved") as Global, struct).expect("writeGlobal")
        val saved = invoke(store, instance, "saved_x", emptyList()).expect("saved_x")

        assertEquals(FieldValue.Execution(I32(9)), field)
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
        function: HostFunction,
        signature: FunctionTypeBuilder.() -> Unit,
    ): Import {
        return functionImport(store) {
            moduleName = "host"
            entityName = name
            type(signature)
            reference(function)
        }
    }

    private val inspect = HostFunction { parameters, results ->
        val point = parameters.readRawReference(0)
        val sum = withGc {
            val next = readStructField(point, NEXT)
            val sum = readStructField(point, X).toInt() + readStructField(point, TAG).toInt() + readStructField(next, X).toInt()
            writeStructField(point, X, 1000L)
            sum
        }
        results.writeI32(0, sum)
    }

    private val make = HostFunction { _, results ->
        val module = contextOf<HostModuleInstance>()
        val point = withGc {
            val label = withExterns { create(LABEL).raw }
            allocateStruct(runtimeType(module, POINT), longArrayOf(5L, 0L, NULL_STRUCT, label))
        }
        results.writeRawReference(0, point)
    }

    private val raise = HostFunction { parameters, _ ->
        val payload = longArrayOf(parameters.readI32(0) * 2L, withExterns { nullReference().raw })
        withTag(ModuleIndex.TagIndex(0)) {
            val tag = this
            withExceptions { raise(create(tag, payload, 0)) }
        }
    }

    private val label = HostFunction { parameters, results ->
        val label = withExterns { value(HostExternReference(parameters.readRawReference(0))) }
        results.writeI32(0, if (label == LABEL) 1 else 0)
    }

    private companion object {
        const val POINT = 0
        const val X = 0
        const val TAG = 1
        const val NEXT = 2
        const val LABEL = "hello"
        val NULL_STRUCT = ReferenceValue.Null(AbstractHeapType.Struct).toLongFromBoxed()
    }
}
