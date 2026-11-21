package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.compiled.Import
import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.PreparedFunction
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.function
import io.github.charlietap.chasm.embedding.compiled.host.HostFunction
import io.github.charlietap.chasm.embedding.compiled.host.readF64
import io.github.charlietap.chasm.embedding.compiled.host.readI32
import io.github.charlietap.chasm.embedding.compiled.host.readReference
import io.github.charlietap.chasm.embedding.compiled.host.writeI32
import io.github.charlietap.chasm.embedding.compiled.host.writeReference
import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.compiled.invoke
import io.github.charlietap.chasm.embedding.compiled.invokeUnboxed
import io.github.charlietap.chasm.embedding.compiled.prepareFunction
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.F64
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.compiled.value.ReferenceValue
import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.type.FunctionType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame

class ReferenceLaneTest {

    private val store = store()
    private var echoed: Any? = null
    private var reentry: PreparedFunction? = null
    private val instance by lazy { instantiate() }

    @Test
    fun guestStructsCrossHostFunctionsByIdentity() {

        val call = prepare("echo_same")
        val words = LongArray(call.slots)

        call.invokeUnboxed(words, arrayOfNulls(call.slots)).expect("echo_same")

        assertEquals(1, words[0].toInt())
        assertIs<StructReference>(echoed)
    }

    @Test
    fun hostValuesSurviveCollectionsInsideTheGuest() {

        val value = Any()
        val park = prepare("park")
        val parked = prepare("parked")
        val references = arrayOfNulls<Any>(1)
        references[0] = value

        park.invokeUnboxed(LongArray(1), references).expect("park")
        references[0] = null
        System.gc()
        parked.invokeUnboxed(LongArray(1), references).expect("parked")

        assertSame(value, references[0])
    }

    @Test
    fun numbersAndReferencesShareSlotPositions() {

        val call = prepare("mix")
        val words = LongArray(call.slots)
        val references = arrayOfNulls<Any>(call.slots)
        words[0] = 7L
        references[1] = "value"
        words[2] = 2.5.toRawBits()

        call.invokeUnboxed(words, references).expect("mix")

        assertEquals("7:2.5:value", references[0])
    }

    @Test
    fun nullReferencesPassThrough() {

        val call = prepare("identity")
        val references = arrayOfNulls<Any>(1)

        call.invokeUnboxed(LongArray(1), references).expect("identity")

        assertNull(references[0])
    }

    @Test
    fun numericCallsTakeNoReferenceSlots() {

        val call = prepare("add")
        val words = longArrayOf(40L, 2L)

        call.invokeUnboxed(words, arrayOfNulls(0)).expect("add")

        assertEquals(42, words[0].toInt())
    }

    @Test
    fun hostFunctionsCanCallBackIntoTheGuest() {

        reentry = prepare("identity")
        val call = prepare("reenter")
        val value = Any()
        val references = arrayOfNulls<Any>(1)
        references[0] = value

        call.invokeUnboxed(LongArray(1), references).expect("reenter")

        assertSame(value, references[0])
    }

    @Test
    fun trapsAndExceptionsFailOnlyTheirCall() {

        val trap = prepare("trap").invokeUnboxed(LongArray(0), arrayOfNulls(0))
        val thrown = prepare("throw").invokeUnboxed(LongArray(0), arrayOfNulls(0))
        val words = longArrayOf(1L, 2L)
        prepare("add").invokeUnboxed(words, arrayOfNulls(0)).expect("add")

        assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(trap).error)
        assertIs<ExecutionError.Thrown<*>>(assertIs<ChasmResult.Error<*>>(thrown).error)
        assertEquals(3, words[0].toInt())
    }

    @Test
    fun exportedHostFunctionsAnswerBoxedCalls() {

        val result = invoke(store, instance, "mix_import", listOf(I32(3), ReferenceValue.Extern("value"), F64(0.5))).expect("mix_import")

        assertEquals("3:0.5:value", assertIs<ReferenceValue.Extern>(result.single()).value)
    }

    private fun prepare(name: String): PreparedFunction {
        return prepareFunction(store, instance, name).expect(name)
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
                function(
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
                function(
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
                function(
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
                function(
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

    private val echo = HostFunction { parameters, results ->
        val reference = parameters.readReference(0)
        echoed = reference
        results.writeReference(0, reference)
    }

    private val mix = HostFunction { parameters, results ->
        val label = "${parameters.readI32(0)}:${parameters.readF64(2)}:${parameters.readReference(1)}"
        results.writeReference(0, label)
    }

    private val add = HostFunction { parameters, results ->
        results.writeI32(0, parameters.readI32(0) + parameters.readI32(1))
    }

    private val reenter = HostFunction { parameters, results ->
        val references = arrayOfNulls<Any>(1)
        references[0] = parameters.readReference(0)
        checkNotNull(reentry).invokeUnboxed(LongArray(1), references).expect("identity")
        results.writeReference(0, references[0])
    }
}

private fun functionType(
    builder: FunctionTypeBuilder.() -> Unit,
): FunctionType {
    return FunctionTypeBuilder().apply(builder).build()
}
