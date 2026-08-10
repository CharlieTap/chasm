package io.github.charlietap.chasm.runtime.function

import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class WasmFunctionCallPlanTest {

    @Test
    fun installsAPlanOnce() {
        val plan = WasmFunctionCallPlan(
            params = 1,
            results = 1,
            interfaceSlots = 1,
            module = moduleInstance(),
            locals = longArrayOf(0),
        )

        plan.install(entryIp = 7, frameSlots = 3)

        assertTrue(plan.isInstalled)
        assertEquals(7, plan.entryIp)
        assertEquals(3, plan.frameSlots)
        assertFailsWith<IllegalStateException> {
            plan.install(entryIp = 8, frameSlots = 4)
        }
    }
}
