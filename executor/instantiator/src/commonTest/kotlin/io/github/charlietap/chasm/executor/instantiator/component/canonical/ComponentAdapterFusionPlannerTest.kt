package io.github.charlietap.chasm.executor.instantiator.component.canonical

import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.component.canonical.canonicalValueTupleLayout
import io.github.charlietap.chasm.fixture.runtime.component.canonical.linearMemoryLiftPlan
import io.github.charlietap.chasm.fixture.runtime.component.canonical.linearMemoryLowerPlan
import io.github.charlietap.chasm.fixture.type.component.primitiveComponentValueType
import io.github.charlietap.chasm.runtime.component.canonical.LowerParameterPassing
import io.github.charlietap.chasm.type.component.ComponentPrimitiveType
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentAdapterFusionPlannerTest {

    @Test
    fun `fuses direct flat scalar adapters`() {
        val tuple = canonicalValueTupleLayout(layouts = intArrayOf(0), flatCount = 1)
        val lower = linearMemoryLowerPlan(parameterTuple = tuple, resultTuple = tuple)
        val lift = linearMemoryLiftPlan(parameterTuple = tuple, resultTuple = tuple)
        val layoutCompiler = Memory32LayoutCompiler()
        layoutCompiler.compile(primitiveComponentValueType(ComponentPrimitiveType.U32)).unwrap()

        val actual = CanFuseComponentAdapter(runtimeConfig(), layoutCompiler, lower, lift)

        val expected = true
        assertEquals(expected, actual)
    }

    @Test
    fun `keeps unsupported adapters on the general path`() {
        val scalarTuple = canonicalValueTupleLayout(layouts = intArrayOf(0), flatCount = 1)
        val stringTuple = canonicalValueTupleLayout(layouts = intArrayOf(1), flatCount = 2)
        val directLower = linearMemoryLowerPlan(parameterTuple = scalarTuple, resultTuple = scalarTuple)
        val indirectLower = linearMemoryLowerPlan(
            parameterTuple = scalarTuple,
            resultTuple = scalarTuple,
            parameterPassing = LowerParameterPassing.IndirectPointer,
        )
        val stringLower = linearMemoryLowerPlan(parameterTuple = stringTuple, resultTuple = stringTuple)
        val scalarLift = linearMemoryLiftPlan(parameterTuple = scalarTuple, resultTuple = scalarTuple)
        val stringLift = linearMemoryLiftPlan(parameterTuple = stringTuple, resultTuple = stringTuple)
        val layoutCompiler = Memory32LayoutCompiler()
        layoutCompiler.compile(primitiveComponentValueType(ComponentPrimitiveType.U32)).unwrap()
        layoutCompiler.compile(primitiveComponentValueType(ComponentPrimitiveType.String)).unwrap()

        val actual = listOf(
            CanFuseComponentAdapter(runtimeConfig(bytecodeFusion = false), layoutCompiler, directLower, scalarLift),
            CanFuseComponentAdapter(runtimeConfig(), layoutCompiler, indirectLower, scalarLift),
            CanFuseComponentAdapter(runtimeConfig(), layoutCompiler, stringLower, stringLift),
        )

        val expected = listOf(false, false, false)
        assertEquals(expected, actual)
    }
}
