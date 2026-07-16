package io.github.charlietap.chasm.executor.instantiator.component.canonical

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.component.CanonicalDefinition
import io.github.charlietap.chasm.executor.instantiator.component.ComponentPlanningContext
import io.github.charlietap.chasm.executor.instantiator.component.initializer.ComponentInitializer
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreExternalValue
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreFunctionSource
import io.github.charlietap.chasm.executor.instantiator.component.invalidPreparation
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerFrame
import io.github.charlietap.chasm.executor.instantiator.component.unsupported
import io.github.charlietap.chasm.runtime.component.error.ComponentPreparationError
import io.github.charlietap.chasm.runtime.component.error.UnsupportedComponentFeature
import io.github.charlietap.chasm.runtime.component.function.PreparedComponentFunction
import io.github.charlietap.chasm.runtime.component.index.PreparedComponentFunctionIndex
import io.github.charlietap.chasm.runtime.component.resource.CanonicalResourceFunction
import io.github.charlietap.chasm.runtime.component.resource.CanonicalResourceFunctionKind
import io.github.charlietap.chasm.type.component.ComponentDefinedType
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiDescriptor

internal fun planCanonical(
    context: ComponentPlanningContext,
    frame: PlannerFrame,
    canonical: CanonicalDefinition,
): Result<Unit, ComponentPreparationError> = when (canonical) {
    is CanonicalDefinition.Lift -> planCanonicalLift(context, frame, canonical)
    is CanonicalDefinition.Lower -> planCanonicalLower(context, frame, canonical)
    is CanonicalDefinition.ResourceNew -> planCanonicalResource(
        context,
        frame,
        canonical.typeIndex.idx.toInt(),
        CanonicalResourceFunctionKind.ResourceNew,
    )
    is CanonicalDefinition.ResourceDrop -> planCanonicalResource(
        context,
        frame,
        canonical.typeIndex.idx.toInt(),
        CanonicalResourceFunctionKind.ResourceDrop,
    )
    is CanonicalDefinition.ResourceRep -> planCanonicalResource(
        context,
        frame,
        canonical.typeIndex.idx.toInt(),
        CanonicalResourceFunctionKind.ResourceRep,
    )
    is CanonicalDefinition.StreamNew,
    is CanonicalDefinition.StreamRead,
    is CanonicalDefinition.StreamWrite,
    is CanonicalDefinition.StreamCancelRead,
    is CanonicalDefinition.StreamCancelWrite,
    is CanonicalDefinition.StreamDropReadable,
    is CanonicalDefinition.StreamDropWritable,
    is CanonicalDefinition.FutureNew,
    is CanonicalDefinition.FutureRead,
    is CanonicalDefinition.FutureWrite,
    is CanonicalDefinition.FutureCancelRead,
    is CanonicalDefinition.FutureCancelWrite,
    is CanonicalDefinition.FutureDropReadable,
    is CanonicalDefinition.FutureDropWritable,
    -> unsupported(UnsupportedComponentFeature.Stream)
    is CanonicalDefinition.ErrorContextNew,
    is CanonicalDefinition.ErrorContextDebugMessage,
    CanonicalDefinition.ErrorContextDrop,
    -> unsupported(UnsupportedComponentFeature.ErrorContext)
    is CanonicalDefinition.ThreadIndex,
    is CanonicalDefinition.ThreadNewIndirect,
    is CanonicalDefinition.ThreadResumeLater,
    is CanonicalDefinition.ThreadSuspend,
    is CanonicalDefinition.ThreadYield,
    is CanonicalDefinition.ThreadSuspendThenResume,
    is CanonicalDefinition.ThreadYieldThenResume,
    is CanonicalDefinition.ThreadSuspendThenPromote,
    is CanonicalDefinition.ThreadYieldThenPromote,
    is CanonicalDefinition.ThreadSpawnRef,
    is CanonicalDefinition.ThreadSpawnIndirect,
    is CanonicalDefinition.ThreadAvailableParallelism,
    -> unsupported(UnsupportedComponentFeature.Thread)
    CanonicalDefinition.BackpressureSet,
    CanonicalDefinition.BackpressureInc,
    CanonicalDefinition.BackpressureDec,
    is CanonicalDefinition.TaskReturn,
    CanonicalDefinition.TaskCancel,
    is CanonicalDefinition.ContextGet,
    is CanonicalDefinition.ContextSet,
    is CanonicalDefinition.SubtaskCancel,
    CanonicalDefinition.SubtaskDrop,
    CanonicalDefinition.WaitableSetNew,
    is CanonicalDefinition.WaitableSetWait,
    is CanonicalDefinition.WaitableSetPoll,
    CanonicalDefinition.WaitableSetDrop,
    CanonicalDefinition.WaitableJoin,
    -> unsupported(UnsupportedComponentFeature.Async)
}

private fun planCanonicalResource(
    context: ComponentPlanningContext,
    frame: PlannerFrame,
    typeIndex: Int,
    kind: CanonicalResourceFunctionKind,
): Result<Unit, ComponentPreparationError> = binding {
    frame.nextCanonicalDescriptor().bind()
    val resourceType = frame.componentTypes.getOrNull(typeIndex)?.resourceType
        ?: invalidPreparation("canonical resource function references a non-resource type")
            .let { error -> Err(error).bind() }
    val function = context.nextCoreFunction()
    context.initializers += ComponentInitializer.ResourceFunction(
        function = function,
        resource = CanonicalResourceFunction(
            kind = kind,
            owner = frame.owner,
            resourceType = resourceType,
        ),
    )
    frame.coreFunctions += PreparedCoreExternalValue.Function(
        PreparedCoreFunctionSource.Lowered(function),
    )
}

private fun planCanonicalLift(
    context: ComponentPlanningContext,
    frame: PlannerFrame,
    canonical: CanonicalDefinition.Lift,
): Result<Unit, ComponentPreparationError> = binding {
    val type = frame.componentTypes.getOrNull(canonical.typeIndex.idx.toInt())
        ?.type
        ?.type as? ComponentDefinedType.Function
        ?: invalidPreparation("canon lift references a non-function component type").let { error -> Err(error).bind() }
    val source = frame.coreFunctions.getOrNull(canonical.functionIndex.idx.toInt())
        .toResultOr { invalidPreparation("canon lift references an unknown core function") }
        .bind()
    val descriptor = frame.nextCanonicalDescriptor().bind()
    val options = context.canonicalOptions(frame, canonical.options).bind()
    val coreFunction = context.extractCoreFunction(source)
    val plan = CompileLinearMemoryLiftPlan(
        layoutCompiler = context.layoutCompiler,
        resourceType = frame::resourceType,
        functionType = type.type,
        descriptor = descriptor,
        options = options,
        coreFunctionSlot = coreFunction.index,
    ).bind()
    context.callPlans += plan

    val functionIndex = PreparedComponentFunctionIndex(context.functions.size)
    context.functions += PreparedComponentFunction.LiftedCore(
        liftPlan = plan,
        entryPolicy = context.hostEntryPolicy(plan.optionOwner),
    )
    frame.functions += functionIndex
}

private fun planCanonicalLower(
    context: ComponentPlanningContext,
    frame: PlannerFrame,
    canonical: CanonicalDefinition.Lower,
): Result<Unit, ComponentPreparationError> = binding {
    val target = frame.functions.getOrNull(canonical.functionIndex.idx.toInt())
        .toResultOr { invalidPreparation("canon lower references an unknown component function") }
        .bind()
    val function = context.functions.getOrNull(target.index)
        .toResultOr { invalidPreparation("canon lower references an unprepared component function") }
        .bind()
    val type = when (function) {
        is PreparedComponentFunction.LiftedCore -> function.liftPlan.functionType
        is PreparedComponentFunction.HostImport -> function.functionType
    }
    val descriptor = frame.nextCanonicalDescriptor().bind()
    val options = context.canonicalOptions(frame, canonical.options).bind()
    val compiledPlan = CompileLinearMemoryLowerPlan(
        layoutCompiler = context.layoutCompiler,
        resourceType = frame::resourceType,
        functionType = type,
        descriptor = descriptor,
        options = options,
        targetFunctionSlot = target.index,
        entryPolicy = context.entryPolicy(frame.owner, function.owner),
    ).bind()
    val plan = compiledPlan.copy(
        fusedTarget = (function as? PreparedComponentFunction.LiftedCore)
            ?.liftPlan
            ?.takeIf { target ->
                CanFuseComponentAdapter(
                    context.config,
                    context.layoutCompiler,
                    compiledPlan,
                    target,
                )
            },
    )
    val callPlan = context.callPlans.size
    context.callPlans += plan

    val coreFunction = context.nextCoreFunction()
    context.initializers += ComponentInitializer.LowerImport(coreFunction, callPlan)
    frame.coreFunctions += PreparedCoreExternalValue.Function(
        PreparedCoreFunctionSource.Lowered(coreFunction),
    )
}

private fun PlannerFrame.nextCanonicalDescriptor(): Result<CanonicalAbiDescriptor, ComponentPreparationError> {
    val descriptor = types.canonicalAbi.getOrNull(canonicalAbiIndex)
        ?: return Err(invalidPreparation("canonical index space does not match validated types"))
    canonicalAbiIndex += 1
    return com.github.michaelbull.result.Ok(descriptor)
}
