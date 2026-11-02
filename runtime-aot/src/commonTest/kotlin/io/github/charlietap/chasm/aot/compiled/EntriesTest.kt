package io.github.charlietap.chasm.aot.compiled

import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import kotlin.test.Test
import kotlin.test.assertEquals

class EntriesTest {

    @Test
    fun `functions of one type share an entry class and are each called through it`() {
        val fixture = CompiledFixture(fixture("entries"))

        assertEquals(2, fixture.generated.classes.keys.count { name -> name.substringAfterLast('/').startsWith("Entries") })
        assertEquals(listOf(I32(2_080)), fixture.call("loop", I32(64), I32(63)))
        assertEquals(listOf(I32(42)), fixture.call("inc", I32(41)))
    }
}
