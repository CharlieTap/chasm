package io.github.charlietap.chasm.executor.invoker.component

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.executor.invoker.RawFunctionInvoker
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalCallContext
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalFlatLiftValidator
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalFlatLifter
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalFlatLowerValidator
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalFlatLowerer
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalInvocationException
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalMemoryLifter
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalMemoryLowerer
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLowerPlan
import io.github.charlietap.chasm.runtime.component.canonical.LowerParameterPassing
import io.github.charlietap.chasm.runtime.component.canonical.LowerResultPassing
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.function.PreparedComponentFunction
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction
import io.github.charlietap.chasm.runtime.component.index.PreparedComponentFunctionIndex
import io.github.charlietap.chasm.runtime.component.info.ComponentRuntimeInfo
import io.github.charlietap.chasm.runtime.component.store.ComponentStore
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.function.StackFunctionBody
import io.github.charlietap.chasm.runtime.store.identity

fun LoweredComponentFunctionBody(
    componentStore: ComponentStore,
    root: ComponentRootAddress,
    runtimeInfo: ComponentRuntimeInfo,
    plan: LinearMemoryLowerPlan,
): StackFunctionBody = StackFunctionBody { vstack, _, store, execution ->
    val state = componentStore.runtimeState(root).fold(
        success = { it },
        failure = { it.asCoreTrap() },
    )
    val owner = plan.optionOwner.index
    if (!state.states.mayLeave[owner]) {
        ComponentInvocationError.InvalidCanonicalValue("canonical call may not leave the component instance").asCoreTrap()
    }

    val target = runtimeInfo.functions.getOrNull(plan.targetFunctionSlot)
        ?: ComponentInvocationError.MissingCanonicalDependency("component function").asCoreTrap()
    val targetOwner = when (target) {
        is PreparedComponentFunction.HostImport -> target.owner
        is PreparedComponentFunction.LiftedCore -> target.liftPlan.optionOwner
    }
    if (state.states.poisoned[targetOwner.index]) {
        ComponentInvocationError.CannotEnterComponentInstance.asCoreTrap()
    }
    val scope = componentStore.enterCall(root, plan.optionOwner, targetOwner)
    val scratch = scope.scratch
    val cleanupError: ComponentInvocationError?
    val outcome = try {
        enterComponentInstance(state.states, plan.entryPolicy, scope)
        val flatParameterCount = when (plan.parameterPassing) {
            LowerParameterPassing.Direct -> plan.parameterTuple.flatCount
            LowerParameterPassing.IndirectPointer -> 1
        }
        val coreArguments = scratch.slots(flatParameterCount)
        repeat(flatParameterCount) { index -> coreArguments[index] = vstack.getFrameSlot(index) }
        val preparedImport = runtimeInfo.functions.getOrNull(plan.targetFunctionSlot) as? PreparedComponentFunction.HostImport
        val preparedHost = preparedImport?.let { function ->
            state.hostFunctions.getOrNull(function.importSlot) as? RuntimeComponentHostFunction.Prepared
        }
        if (preparedHost != null) {
            if (
                !preparedImport.preparedHostCompatible ||
                plan.parameterPassing != LowerParameterPassing.Direct ||
                plan.resultPassing != LowerResultPassing.Direct
            ) {
                throw CanonicalInvocationException(
                    ComponentInvocationError.InvalidCanonicalValue(
                        "prepared host function requires a direct memory-free canonical signature",
                    ),
                )
            }
            CanonicalFlatLiftValidator(runtimeInfo, plan.parameterTuple, coreArguments, flatParameterCount)
            val flatResults = scratch.callSlots(plan.resultTuple.flatCount)
            val resultCount = preparedHost(
                scope.hostFunctionContext(store.identity(), root),
                coreArguments,
                flatParameterCount,
                flatResults,
            ).fold(
                success = { it },
                failure = { it.asCoreTrap() },
            )
            CanonicalFlatLowerValidator(runtimeInfo, plan.resultTuple, flatResults, resultCount)
            repeat(resultCount) { index -> vstack.setFrameSlot(index, flatResults[index]) }
        } else {
            val context = CanonicalCallContext(
                config = execution.config,
                store = store,
                componentStore = componentStore,
                root = root,
                owner = plan.optionOwner,
                runtimeInfo = runtimeInfo,
                state = state,
                encoding = plan.encoding,
                memorySlot = plan.memorySlot,
                reallocSlot = plan.reallocSlot,
                scratch = scratch,
                scope = scope,
                coreInvoker = ::RawFunctionInvoker,
            )
            val arguments = when (plan.parameterPassing) {
                LowerParameterPassing.Direct -> CanonicalFlatLifter(context, plan.parameterTuple, coreArguments)
                LowerParameterPassing.IndirectPointer -> CanonicalMemoryLifter(
                    context,
                    plan.parameterTuple,
                    coreArguments[0].toInt(),
                )
            }
            val outputPointer = if (plan.resultPassing == LowerResultPassing.IndirectPointer) {
                vstack.getFrameSlot(flatParameterCount).toInt()
            } else {
                0
            }

            val results = invokePreparedFunction(
                config = execution.config,
                store = store,
                componentStore = componentStore,
                root = root,
                runtimeInfo = runtimeInfo,
                state = state,
                function = PreparedComponentFunctionIndex(plan.targetFunctionSlot),
                arguments = arguments,
                scope = scope,
                coreInvoker = ::RawFunctionInvoker,
            ).fold(
                success = { it },
                failure = { it.asCoreTrap() },
            )

            state.states.mayLeave[owner] = false
            when (plan.resultPassing) {
                LowerResultPassing.Direct -> {
                    val flatResults = scratch.callSlots(plan.resultTuple.flatCount)
                    val resultCount = CanonicalFlatLowerer(context, plan.resultTuple, results, flatResults)
                    repeat(resultCount) { index -> vstack.setFrameSlot(index, flatResults[index]) }
                }
                LowerResultPassing.IndirectPointer -> CanonicalMemoryLowerer(
                    context,
                    plan.resultTuple,
                    results,
                    outputPointer,
                )
            }
            state.states.mayLeave[owner] = true
        }
        null
    } catch (exception: CanonicalInvocationException) {
        state.states.mayLeave[owner] = true
        exception.error
    } catch (exception: InvocationException) {
        state.states.mayLeave[owner] = true
        ComponentInvocationError.CoreTrap(exception.error)
    } catch (exception: ComponentCallScopeException) {
        state.states.mayLeave[owner] = true
        exception.error
    } finally {
        cleanupError = exitComponentCall(componentStore, scope)
    }
    val error = cleanupError ?: outcome
    if (error != null) state.states.poisoned[targetOwner.index] = true
    error?.asCoreTrap()
}
