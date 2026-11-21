@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.extern.createExternRef
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.readF64
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.runtime.value.NumberValue.F64
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.FunctionType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ReferenceLaneTest {

    private val store = store()
    private var kindSeen = WasmReferenceKind.Null
    private var reentry: PreparedCall? = null
    private val instance by lazy { instantiate() }

    @Test
    fun guestStructsCrossHostFunctionsByIdentity() {

        val call = prepare("echo_same")
        val stack = LongArray(call.slots)

        call.call(stack, ReferenceSlots(call.slots)).expect("echo_same")

        assertEquals(1, stack[0].toInt())
        assertEquals(WasmReferenceKind.Struct, kindSeen)
    }

    @Test
    fun hostValuesSurviveCollectionsInsideTheGuest() {

        val value = Any()
        val park = prepare("park")
        val parked = prepare("parked")
        val references = ReferenceSlots(1)
        references[0] = externReference(value)

        park.call(LongArray(1), references).expect("park")
        references[0] = WasmReference.Null
        System.gc()
        parked.call(LongArray(1), references).expect("parked")

        assertEquals(WasmReferenceKind.HostValue, references[0].kind)
        assertSame(value, hostValue(store, references[0]))
    }

    @Test
    fun numbersAndReferencesShareSlotPositions() {

        val call = prepare("mix")
        val stack = LongArray(call.slots)
        val references = ReferenceSlots(call.slots)
        stack[0] = 7L
        references[1] = externReference("value")
        stack[2] = 2.5.toRawBits()

        call.call(stack, references).expect("mix")

        assertEquals("7:2.5:value", hostValue(store, references[0]))
    }

    @Test
    fun nullReferencesPassThrough() {

        val call = prepare("identity")
        val references = ReferenceSlots(1)

        call.call(LongArray(1), references).expect("identity")

        assertTrue(references[0].isNull)
    }

    @Test
    fun numericCallsTakeNoReferenceSlots() {

        val call = prepare("add")
        val stack = longArrayOf(40L, 2L)

        call.call(stack, ReferenceSlots(0)).expect("add")

        assertEquals(42, stack[0].toInt())
    }

    @Test
    fun hostFunctionsCanCallBackIntoTheGuest() {

        reentry = prepare("identity")
        val call = prepare("reenter")
        val value = Any()
        val references = ReferenceSlots(1)
        references[0] = externReference(value)

        call.call(LongArray(1), references).expect("reenter")

        assertSame(value, hostValue(store, references[0]))
    }

    @Test
    fun trapsAndExceptionsFailOnlyTheirCall() {

        val trap = prepare("trap").call(LongArray(0), ReferenceSlots(0))
        val thrown = prepare("throw").call(LongArray(0), ReferenceSlots(0))
        val stack = longArrayOf(1L, 2L)
        prepare("add").call(stack, ReferenceSlots(0)).expect("add")

        assertIs<ChasmResult.Error<*>>(trap)
        assertIs<ChasmResult.Error<*>>(thrown)
        assertEquals(3, stack[0].toInt())
    }

    @Test
    fun referenceFunctionsStillAnswerTheEmbeddingApi() {

        val extern = createExternRef(store, "value").expect("extern").asValue().expect("value")

        val result = invoke(store, instance, "mix_import", listOf(I32(3), extern, F64(0.5))).expect("mix_import")

        val reference = wasmReference(store, result.single() as ReferenceValue)
        assertEquals("3:0.5:value", hostValue(store, reference))
    }

    private fun prepare(name: String): PreparedCall {
        return prepareCall(store, instance, name).expect(name)
    }

    private fun instantiate(): Instance {

        val module = runBlocking { module(resourceBytes("modules/reference_lane.wasm"), validate = true) }.expect("module")

        return runBlocking { instance(store, module, imports(store)) }.expect("instance")
    }

    private fun imports(store: Store): List<Import> {
        return listOf(
            Import(
                "host",
                "echo",
                referenceFunction(
                    store,
                    functionType {
                        params { structref() }
                        results { structref() }
                    },
                    echo,
                ),
            ),
            Import(
                "host",
                "mix",
                referenceFunction(
                    store,
                    functionType {
                        params {
                            i32()
                            externref()
                            f64()
                        }
                        results { externref() }
                    },
                    mix,
                ),
            ),
            Import(
                "host",
                "add",
                referenceFunction(
                    store,
                    functionType {
                        params {
                            i32()
                            i32()
                        }
                        results { i32() }
                    },
                    add,
                ),
            ),
            Import(
                "host",
                "reenter",
                referenceFunction(
                    store,
                    functionType {
                        params { externref() }
                        results { externref() }
                    },
                    reenter,
                ),
            ),
        )
    }

    private val echo = ReferenceHostFunction { parameters, results ->
        val reference = parameters.readReference(0)
        kindSeen = reference.kind
        results.writeReference(0, reference)
    }

    private val mix = ReferenceHostFunction { parameters, results ->
        val label = "${parameters.readI32(0)}:${parameters.readF64(2)}:${hostValue(store, parameters.readReference(1))}"
        results.writeReference(0, externReference(label))
    }

    private val add = ReferenceHostFunction { parameters, results ->
        results.writeI32(0, parameters.readI32(0) + parameters.readI32(1))
    }

    private val reenter = ReferenceHostFunction { parameters, results ->
        val references = ReferenceSlots(1)
        references[0] = parameters.readReference(0)
        checkNotNull(reentry).call(LongArray(1), references).expect("identity")
        results.writeReference(0, references[0])
    }
}

private fun functionType(
    builder: FunctionTypeBuilder.() -> Unit,
): FunctionType {
    return FunctionTypeBuilder().apply(builder).build()
}
