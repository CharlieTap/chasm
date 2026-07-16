package io.github.charlietap.chasm.executor.instantiator.component.canonical

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.runtime.component.canonical.LiftParameterPassing
import io.github.charlietap.chasm.runtime.component.canonical.LiftResultPassing
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryCanonicalOptions
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLiftPlan
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLowerPlan
import io.github.charlietap.chasm.runtime.component.canonical.LowerParameterPassing
import io.github.charlietap.chasm.runtime.component.canonical.LowerResultPassing
import io.github.charlietap.chasm.runtime.component.error.ComponentPreparationError
import io.github.charlietap.chasm.runtime.component.error.UnsupportedComponentFeature
import io.github.charlietap.chasm.runtime.component.function.ComponentEntryPolicy
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.component.ComponentFunctionType
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId
import io.github.charlietap.chasm.type.component.ComponentValueType
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiContext
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiDescriptor
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiSignatureOptions
import io.github.charlietap.chasm.type.component.canonical.CanonicalFunctionTypeLowering
import io.github.charlietap.chasm.type.component.canonical.MAX_FLAT_PARAMS
import io.github.charlietap.chasm.type.component.canonical.MAX_FLAT_RESULTS

fun CompileLinearMemoryLiftPlan(
    layoutCompiler: Memory32LayoutCompiler,
    resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex? = { null },
    functionType: ComponentFunctionType,
    descriptor: CanonicalAbiDescriptor,
    options: LinearMemoryCanonicalOptions,
    coreFunctionSlot: Int,
): Result<LinearMemoryLiftPlan, ComponentPreparationError> = binding {
    val prepared = prepareCanonicalCall(
        layoutCompiler = layoutCompiler,
        resourceType = resourceType,
        functionType = functionType,
        descriptor = descriptor,
        options = options,
        context = CanonicalAbiContext.Lift,
    ).bind()

    LinearMemoryLiftPlan(
        functionType = functionType,
        optionOwner = options.optionOwner,
        coreFunctionSlot = coreFunctionSlot,
        parameterTuple = layoutCompiler.tuple(prepared.parameterLayouts),
        resultTuple = layoutCompiler.tuple(prepared.resultLayouts),
        parameterPassing = if (prepared.parametersIndirect) {
            LiftParameterPassing.IndirectTuple
        } else {
            LiftParameterPassing.Direct
        },
        resultPassing = if (prepared.resultsIndirect) {
            LiftResultPassing.IndirectPointer
        } else {
            LiftResultPassing.Direct
        },
        encoding = options.encoding,
        memorySlot = options.memorySlot,
        reallocSlot = options.reallocSlot,
        postReturnSlot = options.postReturnSlot,
    )
}

fun CompileLinearMemoryLowerPlan(
    layoutCompiler: Memory32LayoutCompiler,
    resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex? = { null },
    functionType: ComponentFunctionType,
    descriptor: CanonicalAbiDescriptor,
    options: LinearMemoryCanonicalOptions,
    targetFunctionSlot: Int,
    entryPolicy: ComponentEntryPolicy,
): Result<LinearMemoryLowerPlan, ComponentPreparationError> = binding {
    if (options.hasPostReturn) {
        invalidOptions<Unit>("canon lower does not accept a post-return option").bind()
    }
    val prepared = prepareCanonicalCall(
        layoutCompiler = layoutCompiler,
        resourceType = resourceType,
        functionType = functionType,
        descriptor = descriptor,
        options = options,
        context = CanonicalAbiContext.Lower,
    ).bind()

    LinearMemoryLowerPlan(
        functionType = functionType,
        optionOwner = options.optionOwner,
        targetFunctionSlot = targetFunctionSlot,
        entryPolicy = entryPolicy,
        parameterTuple = layoutCompiler.tuple(prepared.parameterLayouts),
        resultTuple = layoutCompiler.tuple(prepared.resultLayouts),
        parameterPassing = if (prepared.parametersIndirect) {
            LowerParameterPassing.IndirectPointer
        } else {
            LowerParameterPassing.Direct
        },
        resultPassing = if (prepared.resultsIndirect) {
            LowerResultPassing.IndirectPointer
        } else {
            LowerResultPassing.Direct
        },
        encoding = options.encoding,
        memorySlot = options.memorySlot,
        reallocSlot = options.reallocSlot,
    )
}

private fun prepareCanonicalCall(
    layoutCompiler: Memory32LayoutCompiler,
    resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex?,
    functionType: ComponentFunctionType,
    descriptor: CanonicalAbiDescriptor,
    options: LinearMemoryCanonicalOptions,
    context: CanonicalAbiContext,
): Result<PreparedCanonicalCall, ComponentPreparationError> = binding {
    if (layoutCompiler.addressType == AddressType.I64) {
        unsupported<Unit>(UnsupportedComponentFeature.Memory64).bind()
    }
    if (functionType.async) {
        unsupported<Unit>(UnsupportedComponentFeature.Async).bind()
    }

    val signatureOptions = CanonicalAbiSignatureOptions(addressType = AddressType.I32)
    val lowering = CanonicalFunctionTypeLowering(functionType, signatureOptions, context)
        ?: unavailable<io.github.charlietap.chasm.type.component.canonical.CanonicalAbiLowering>(
            "component function has no canonical memory32 lowering",
        ).bind()
    if (lowering.type != descriptor.type) {
        unavailable<Unit>(CANONICAL_SIGNATURE_MISMATCH).bind()
    }
    if (
        lowering.requiresMemory != descriptor.requiresMemory ||
        lowering.requiresRealloc != descriptor.requiresRealloc
    ) {
        unavailable<Unit>(CANONICAL_REQUIREMENTS_MISMATCH).bind()
    }
    if ((descriptor.requiresMemory || descriptor.requiresRealloc) && !options.hasMemory) {
        invalidOptions<Unit>("canonical option memory is required").bind()
    }
    if (descriptor.requiresRealloc && !options.hasRealloc) {
        invalidOptions<Unit>("canonical option realloc is required").bind()
    }

    val parameterTypes = functionType.params.map { parameter -> parameter.type }
    val resultTypes = functionType.result?.let(::listOf).orEmpty()
    val parameterShape = CanonicalAbiShape(parameterTypes, AddressType.I32)
        ?: unavailable<CanonicalAbiShape>("component parameters have no canonical memory32 shape").bind()
    val resultShape = CanonicalAbiShape(resultTypes, AddressType.I32)
        ?: unavailable<CanonicalAbiShape>("component results have no canonical memory32 shape").bind()

    PreparedCanonicalCall(
        parameterLayouts = compileLayouts(layoutCompiler, parameterTypes, resourceType).bind(),
        resultLayouts = compileLayouts(layoutCompiler, resultTypes, resourceType).bind(),
        parametersIndirect = parameterShape.flatTypes.size > MAX_FLAT_PARAMS,
        resultsIndirect = resultShape.flatTypes.size > MAX_FLAT_RESULTS,
    )
}

private fun compileLayouts(
    compiler: Memory32LayoutCompiler,
    types: List<ComponentValueType>,
    resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex?,
): Result<IntArray, ComponentPreparationError> = binding {
    IntArray(types.size) { index -> compiler.compile(types[index], resourceType).bind().index }
}

private data class PreparedCanonicalCall(
    val parameterLayouts: IntArray,
    val resultLayouts: IntArray,
    val parametersIndirect: Boolean,
    val resultsIndirect: Boolean,
)

private fun <T> unsupported(
    feature: UnsupportedComponentFeature,
): Result<T, ComponentPreparationError> = Err(ComponentPreparationError.UnsupportedFeature(feature))

private fun <T> invalidOptions(reason: String): Result<T, ComponentPreparationError> =
    Err(ComponentPreparationError.InvalidCanonicalOptions(reason))

private fun <T> unavailable(reason: String): Result<T, ComponentPreparationError> =
    Err(ComponentPreparationError.CanonicalLayoutUnavailable(reason))

private const val CANONICAL_SIGNATURE_MISMATCH =
    "generated canonical memory32 signature does not match the validator descriptor"
private const val CANONICAL_REQUIREMENTS_MISMATCH =
    "generated canonical memory32 requirements do not match the validator descriptor"
