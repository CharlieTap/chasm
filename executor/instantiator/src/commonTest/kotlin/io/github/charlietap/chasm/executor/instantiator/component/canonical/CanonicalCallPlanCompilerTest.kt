package io.github.charlietap.chasm.executor.instantiator.component.canonical

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.fixture.runtime.component.canonical.canonicalAbiDescriptorFor
import io.github.charlietap.chasm.fixture.runtime.component.canonical.linearMemoryCanonicalOptions
import io.github.charlietap.chasm.fixture.runtime.component.error.canonicalLayoutUnavailableComponentPreparationError
import io.github.charlietap.chasm.fixture.runtime.component.error.unsupportedFeatureComponentPreparationError
import io.github.charlietap.chasm.fixture.runtime.component.function.componentEntryPolicy
import io.github.charlietap.chasm.fixture.runtime.component.index.runtimeComponentInstanceIndex
import io.github.charlietap.chasm.fixture.type.component.canonical.canonicalAbiDescriptor
import io.github.charlietap.chasm.fixture.type.component.componentFunctionType
import io.github.charlietap.chasm.fixture.type.component.labeledComponentValueType
import io.github.charlietap.chasm.fixture.type.component.primitiveComponentValueType
import io.github.charlietap.chasm.fixture.type.component.tupleComponentValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalStringEncoding
import io.github.charlietap.chasm.runtime.component.canonical.LiftParameterPassing
import io.github.charlietap.chasm.runtime.component.canonical.LiftResultPassing
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLiftPlan
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLowerPlan
import io.github.charlietap.chasm.runtime.component.canonical.LowerParameterPassing
import io.github.charlietap.chasm.runtime.component.canonical.LowerResultPassing
import io.github.charlietap.chasm.runtime.component.error.UnsupportedComponentFeature
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.component.ComponentPrimitiveType
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiContext
import io.github.charlietap.chasm.type.component.canonical.CanonicalCoreFunctionType
import io.github.charlietap.chasm.type.component.canonical.MAX_FLAT_PARAMS
import kotlin.test.Test
import kotlin.test.assertEquals

class CanonicalCallPlanCompilerTest {

    @Test
    fun `compiles direct lift and lower plans with resolved slots`() {
        val functionType = componentFunctionType(
            params = listOf(
                labeledComponentValueType("value", primitiveComponentValueType(ComponentPrimitiveType.U32)),
            ),
            result = primitiveComponentValueType(ComponentPrimitiveType.U64),
        )
        val owner = runtimeComponentInstanceIndex(3)
        val options = linearMemoryCanonicalOptions(
            optionOwner = owner,
            encoding = CanonicalStringEncoding.Utf16,
            memorySlot = 4,
            reallocSlot = 5,
            postReturnSlot = 6,
        )
        val liftLayoutCompiler = Memory32LayoutCompiler()
        val lowerLayoutCompiler = Memory32LayoutCompiler()
        val entryPolicy = componentEntryPolicy()

        val lift = CompileLinearMemoryLiftPlan(
            layoutCompiler = liftLayoutCompiler,
            functionType = functionType,
            descriptor = canonicalAbiDescriptorFor(functionType, CanonicalAbiContext.Lift),
            options = options,
            coreFunctionSlot = 7,
        ).unwrap()
        val lower = CompileLinearMemoryLowerPlan(
            layoutCompiler = lowerLayoutCompiler,
            functionType = functionType,
            descriptor = canonicalAbiDescriptorFor(functionType, CanonicalAbiContext.Lower),
            options = options.copy(postReturnSlot = -1),
            targetFunctionSlot = 10,
            entryPolicy = entryPolicy,
        ).unwrap()
        val actual = CanonicalPlansObservation(
            lift = lift.observation(),
            lower = lower.observation(),
            encoding = lift.encoding,
            liftSlots = listOf(lift.memorySlot, lift.reallocSlot, lift.postReturnSlot),
            lowerSlots = listOf(lower.memorySlot, lower.reallocSlot),
        )

        val expected = CanonicalPlansObservation(
            lift = LiftObservation(3, 7, listOf(0), listOf(1), LiftParameterPassing.Direct, LiftResultPassing.Direct),
            lower = LowerObservation(3, 10, listOf(0), listOf(1), LowerParameterPassing.Direct, LowerResultPassing.Direct),
            encoding = CanonicalStringEncoding.Utf16,
            liftSlots = listOf(4, 5, 6),
            lowerSlots = listOf(4, 5),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `specializes indirect conventions beyond both flattening limits`() {
        val functionType = componentFunctionType(
            params = List(MAX_FLAT_PARAMS + 1) { index ->
                labeledComponentValueType(
                    "parameter-$index",
                    primitiveComponentValueType(ComponentPrimitiveType.S32),
                )
            },
            result = tupleComponentValueType(
                listOf(
                    primitiveComponentValueType(ComponentPrimitiveType.U32),
                    primitiveComponentValueType(ComponentPrimitiveType.U64),
                ),
            ),
        )
        val options = linearMemoryCanonicalOptions(memorySlot = 1, reallocSlot = 2)
        val liftLayoutCompiler = Memory32LayoutCompiler()
        val lowerLayoutCompiler = Memory32LayoutCompiler()
        val entryPolicy = componentEntryPolicy()

        val lift = CompileLinearMemoryLiftPlan(
            layoutCompiler = liftLayoutCompiler,
            functionType = functionType,
            descriptor = canonicalAbiDescriptorFor(functionType, CanonicalAbiContext.Lift),
            options = options,
            coreFunctionSlot = 0,
        ).unwrap()
        val lower = CompileLinearMemoryLowerPlan(
            layoutCompiler = lowerLayoutCompiler,
            functionType = functionType,
            descriptor = canonicalAbiDescriptorFor(functionType, CanonicalAbiContext.Lower),
            options = options,
            targetFunctionSlot = 0,
            entryPolicy = entryPolicy,
        ).unwrap()
        val actual = CanonicalPassingObservation(
            liftParameterPassing = lift.parameterPassing,
            liftResultPassing = lift.resultPassing,
            lowerParameterPassing = lower.parameterPassing,
            lowerResultPassing = lower.resultPassing,
            parameterLayoutCount = lift.parameterTuple.layouts.size,
            resultLayoutCount = lift.resultTuple.layouts.size,
        )

        val expected = CanonicalPassingObservation(
            liftParameterPassing = LiftParameterPassing.IndirectTuple,
            liftResultPassing = LiftResultPassing.IndirectPointer,
            lowerParameterPassing = LowerParameterPassing.IndirectPointer,
            lowerResultPassing = LowerResultPassing.IndirectPointer,
            parameterLayoutCount = MAX_FLAT_PARAMS + 1,
            resultLayoutCount = 1,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `keeps values at the flattening thresholds direct`() {
        val functionType = componentFunctionType(
            params = List(MAX_FLAT_PARAMS) { index ->
                labeledComponentValueType(
                    "parameter-$index",
                    primitiveComponentValueType(ComponentPrimitiveType.S32),
                )
            },
            result = primitiveComponentValueType(ComponentPrimitiveType.S32),
        )
        val liftLayoutCompiler = Memory32LayoutCompiler()
        val lowerLayoutCompiler = Memory32LayoutCompiler()
        val entryPolicy = componentEntryPolicy()

        val lift = CompileLinearMemoryLiftPlan(
            layoutCompiler = liftLayoutCompiler,
            functionType = functionType,
            descriptor = canonicalAbiDescriptorFor(functionType, CanonicalAbiContext.Lift),
            options = linearMemoryCanonicalOptions(),
            coreFunctionSlot = 0,
        ).unwrap()
        val lower = CompileLinearMemoryLowerPlan(
            layoutCompiler = lowerLayoutCompiler,
            functionType = functionType,
            descriptor = canonicalAbiDescriptorFor(functionType, CanonicalAbiContext.Lower),
            options = linearMemoryCanonicalOptions(),
            targetFunctionSlot = 0,
            entryPolicy = entryPolicy,
        ).unwrap()
        val actual = CanonicalPassingObservation(
            liftParameterPassing = lift.parameterPassing,
            liftResultPassing = lift.resultPassing,
            lowerParameterPassing = lower.parameterPassing,
            lowerResultPassing = lower.resultPassing,
            parameterLayoutCount = lift.parameterTuple.layouts.size,
            resultLayoutCount = lift.resultTuple.layouts.size,
        )

        val expected = CanonicalPassingObservation(
            liftParameterPassing = LiftParameterPassing.Direct,
            liftResultPassing = LiftResultPassing.Direct,
            lowerParameterPassing = LowerParameterPassing.Direct,
            lowerResultPassing = LowerResultPassing.Direct,
            parameterLayoutCount = MAX_FLAT_PARAMS,
            resultLayoutCount = 1,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `rejects a validator signature mismatch before compiling layouts`() {
        val functionType = componentFunctionType(
            params = listOf(labeledComponentValueType(type = primitiveComponentValueType())),
        )
        val descriptor = canonicalAbiDescriptor(
            type = CanonicalCoreFunctionType(params = listOf(i64ValueType())),
        )
        val layoutCompiler = Memory32LayoutCompiler()

        val result = CompileLinearMemoryLiftPlan(
            layoutCompiler = layoutCompiler,
            functionType = functionType,
            descriptor = descriptor,
            options = linearMemoryCanonicalOptions(),
            coreFunctionSlot = 0,
        )
        val actual = result to layoutCompiler.layouts.isEmpty()

        val expected = Err(
            canonicalLayoutUnavailableComponentPreparationError(CANONICAL_SIGNATURE_MISMATCH),
        ) to true
        assertEquals(expected, actual)
    }

    @Test
    fun `rejects deferred async and memory64 calls explicitly`() {
        val deferredType = componentFunctionType(
            params = listOf(
                labeledComponentValueType(
                    type = primitiveComponentValueType(ComponentPrimitiveType.ErrorContext),
                ),
            ),
        )
        val asyncType = componentFunctionType(async = true)
        val deferredLayoutCompiler = Memory32LayoutCompiler()
        val asyncLayoutCompiler = Memory32LayoutCompiler()
        val memory64LayoutCompiler = Memory32LayoutCompiler(AddressType.I64)
        val entryPolicy = componentEntryPolicy()

        val actual = listOf(
            CompileLinearMemoryLiftPlan(
                layoutCompiler = deferredLayoutCompiler,
                functionType = deferredType,
                descriptor = canonicalAbiDescriptorFor(deferredType, CanonicalAbiContext.Lift),
                options = linearMemoryCanonicalOptions(),
                coreFunctionSlot = 0,
            ),
            CompileLinearMemoryLowerPlan(
                layoutCompiler = asyncLayoutCompiler,
                functionType = asyncType,
                descriptor = canonicalAbiDescriptorFor(asyncType, CanonicalAbiContext.Lower),
                options = linearMemoryCanonicalOptions(),
                targetFunctionSlot = 0,
                entryPolicy = entryPolicy,
            ),
            CompileLinearMemoryLiftPlan(
                layoutCompiler = memory64LayoutCompiler,
                functionType = componentFunctionType(),
                descriptor = canonicalAbiDescriptorFor(componentFunctionType(), CanonicalAbiContext.Lift),
                options = linearMemoryCanonicalOptions(),
                coreFunctionSlot = 0,
            ),
        )

        val expected = listOf(
            Err(unsupportedFeatureComponentPreparationError(UnsupportedComponentFeature.ErrorContext)),
            Err(unsupportedFeatureComponentPreparationError(UnsupportedComponentFeature.Async)),
            Err(unsupportedFeatureComponentPreparationError(UnsupportedComponentFeature.Memory64)),
        )
        assertEquals(expected, actual)
    }
}

private fun LinearMemoryLiftPlan.observation() = LiftObservation(
    optionOwner = optionOwner.index,
    functionSlot = coreFunctionSlot,
    parameterLayouts = parameterTuple.layouts.toList(),
    resultLayouts = resultTuple.layouts.toList(),
    parameterPassing = parameterPassing,
    resultPassing = resultPassing,
)

private fun LinearMemoryLowerPlan.observation() = LowerObservation(
    optionOwner = optionOwner.index,
    functionSlot = targetFunctionSlot,
    parameterLayouts = parameterTuple.layouts.toList(),
    resultLayouts = resultTuple.layouts.toList(),
    parameterPassing = parameterPassing,
    resultPassing = resultPassing,
)

private data class CanonicalPlansObservation(
    val lift: LiftObservation,
    val lower: LowerObservation,
    val encoding: CanonicalStringEncoding,
    val liftSlots: List<Int>,
    val lowerSlots: List<Int>,
)

private data class CanonicalPassingObservation(
    val liftParameterPassing: LiftParameterPassing,
    val liftResultPassing: LiftResultPassing,
    val lowerParameterPassing: LowerParameterPassing,
    val lowerResultPassing: LowerResultPassing,
    val parameterLayoutCount: Int,
    val resultLayoutCount: Int,
)

private data class LiftObservation(
    val optionOwner: Int,
    val functionSlot: Int,
    val parameterLayouts: List<Int>,
    val resultLayouts: List<Int>,
    val parameterPassing: LiftParameterPassing,
    val resultPassing: LiftResultPassing,
)

private data class LowerObservation(
    val optionOwner: Int,
    val functionSlot: Int,
    val parameterLayouts: List<Int>,
    val resultLayouts: List<Int>,
    val parameterPassing: LowerParameterPassing,
    val resultPassing: LowerResultPassing,
)

private const val CANONICAL_SIGNATURE_MISMATCH =
    "generated canonical memory32 signature does not match the validator descriptor"
