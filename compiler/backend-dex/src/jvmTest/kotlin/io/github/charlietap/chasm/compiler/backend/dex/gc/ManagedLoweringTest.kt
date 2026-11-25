package io.github.charlietap.chasm.compiler.backend.dex.gc

import io.github.charlietap.chasm.compiler.backend.dex.DexOptions
import io.github.charlietap.chasm.compiler.backend.dex.assertGolden
import io.github.charlietap.chasm.compiler.backend.dex.decodeModule
import io.github.charlietap.chasm.compiler.backend.dex.fixture
import io.github.charlietap.chasm.compiler.backend.dex.generate
import io.github.charlietap.chasm.compiler.backend.dex.renderSmali
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ManagedLoweringTest {

    private val smali = renderSmali(generate(decodeModule(fixture("storage")), DexOptions()).dex)

    @Test
    fun `struct new stores each lane`() {

        val method = method(STRUCT_NEW, "struct_new")

        assertTrue("new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/" in method)
        assertEquals(4, PUT_LANE.findAll(method).count())
    }

    @Test
    fun `struct get of an i64 field combines two lanes`() {

        val method = method(GET_I64, "get_i64")

        assertEquals(2, GET_INT_LANE.findAll(method).count())
        assertTrue("shl-long" in method && "or-long" in method)
    }

    @Test
    fun `struct get of an inherited field casts to the type's own class`() {

        val method = method(GET_INHERITED, "get_inherited")
        val cast = checkNotNull(CHECK_CAST.find(method)).groupValues[1]
        val owner = checkNotNull(GET_INT_LANE.find(method)).groupValues[1]

        assertTrue(cast != owner, "the cast names $cast and the lane is declared by $owner")
    }

    @Test
    fun `struct set of a packed field masks the value`() {
        assertTrue("and-int/lit16 v8, v8, 0xff" in method(SET_PACKED, "set_packed"))
    }

    @Test
    fun `a spilled field reads through the spill array`() {

        val method = method(GET_SPILLED, "get_spilled")

        assertTrue("->spilledInts:[I" in method && "aget v8, v11, v15" in method)
    }

    @Test
    fun `ref cast to a final type compares the RTT`() {

        val method = method(CAST_FINAL, "cast_final")

        assertTrue("->rtt:Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;" in method && "if-eq v11, v10" in method)
        assertTrue("->supertypes" !in method)
    }

    @Test
    fun `ref test at depth 2 compares the RTT itself, then checks the depth and the supertype`() {

        val method = method(TEST_DEPTH, "test_depth")

        assertTrue(method.indexOf("if-eq ") in 0 until method.indexOf("->depth:I"))
        assertTrue(method.indexOf("->depth:I") in 0 until method.indexOf("->supertypes"))
        assertTrue("const/4 v9, 0x2" in method)
    }

    @Test
    fun `array new wraps its storage with the RTT`() {
        assertTrue("I8Array;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;[B)V" in method(ARRAY_NEW, "array_new"))
    }

    @Test
    fun `a function testing one type twice caches its RTT`() {

        val method = method(TEST_TWICE, "test_twice")

        assertEquals(1, RTT_BINDING_LOAD.findAll(method).count())
    }

    private fun method(
        function: Int,
        name: String,
    ): String {

        val start = smali.indexOf(".method public static f$function(")
        val method = smali.substring(start, smali.indexOf(".end method", start) + ".end method".length)
        assertGolden("storage_$name.smali", method + "\n")

        return method
    }

    private companion object {
        const val STRUCT_NEW = 0
        const val GET_I64 = 1
        const val GET_INHERITED = 2
        const val SET_PACKED = 3
        const val GET_SPILLED = 4
        const val CAST_FINAL = 5
        const val TEST_DEPTH = 6
        const val ARRAY_NEW = 7
        const val TEST_TWICE = 8
        val PUT_LANE = Regex("iput(-object)? v\\d+, v12, L[^;]+;->[ir]\\d+")
        val GET_INT_LANE = Regex("iget v\\d+, v12, (L[^;]+;)->i\\d+:I")
        val CHECK_CAST = Regex("check-cast v12, (L[^;]+;)")
        val RTT_BINDING_LOAD = Regex("check-cast v\\d+, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;")
    }
}
