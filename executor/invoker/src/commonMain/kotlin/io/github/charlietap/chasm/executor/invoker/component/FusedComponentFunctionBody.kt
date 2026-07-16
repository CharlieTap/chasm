package io.github.charlietap.chasm.executor.invoker.component

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalCoreFunctionInvoker
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalFlatTranscoder
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalInvocationException
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalPostReturnInvoker
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLowerPlan
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.info.ComponentRuntimeInfo
import io.github.charlietap.chasm.runtime.component.store.ComponentStore
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.function.StackFunctionBody

fun FusedComponentFunctionBody(
    componentStore: ComponentStore,
    root: ComponentRootAddress,
    runtimeInfo: ComponentRuntimeInfo,
    plan: LinearMemoryLowerPlan,
): StackFunctionBody {
    val target = checkNotNull(plan.fusedTarget)
    return StackFunctionBody { vstack, cstack, store, execution ->
        val state = componentStore.runtimeState(root).fold(
            success = { it },
            failure = { it.asCoreTrap() },
        )
        val owner = plan.optionOwner.index
        val targetOwner = target.optionOwner.index
        if (!state.states.mayLeave[owner]) {
            ComponentInvocationError.InvalidCanonicalValue("canonical call may not leave the component instance")
                .asCoreTrap()
        }
        if (state.states.poisoned[targetOwner]) {
            ComponentInvocationError.CannotEnterComponentInstance.asCoreTrap()
        }

        val scope = componentStore.enterCall(root, plan.optionOwner, target.optionOwner)
        val scratch = scope.scratch
        val previousTargetMayLeave = state.states.mayLeave[targetOwner]
        val cleanupError: ComponentInvocationError?
        val outcome = try {
            enterComponentInstance(state.states, plan.entryPolicy, scope)

            val parameterCount = plan.parameterTuple.flatCount
            val sourceArguments = scratch.callSlots(parameterCount)
            repeat(parameterCount) { index -> sourceArguments[index] = vstack.getFrameSlot(index) }
            val targetArguments = scratch.slots(parameterCount)
            state.states.mayLeave[targetOwner] = false
            CanonicalFlatTranscoder(
                runtimeInfo,
                plan.parameterTuple,
                sourceArguments,
                parameterCount,
                targetArguments,
            )

            state.states.mayLeave[targetOwner] = previousTargetMayLeave
            val expectedResultCount = target.resultTuple.flatCount
            val coreResults = scratch.callSlots(expectedResultCount)
            val resultCount = CanonicalCoreFunctionInvoker(
                vstack = vstack,
                cstack = cstack,
                store = store,
                execution = execution,
                state = state,
                functionSlot = target.coreFunctionSlot,
                arguments = targetArguments,
                argumentCount = parameterCount,
                results = coreResults,
            )
            if (resultCount != expectedResultCount) {
                throw CanonicalInvocationException(
                    ComponentInvocationError.InvalidCanonicalValue(
                        "core function returned the wrong canonical result count",
                    ),
                )
            }

            state.states.mayLeave[targetOwner] = false
            val adaptedResults = scratch.slots(resultCount)
            CanonicalFlatTranscoder(
                runtimeInfo,
                target.resultTuple,
                coreResults,
                resultCount,
                adaptedResults,
            )
            if (target.postReturnSlot >= 0) {
                CanonicalPostReturnInvoker(
                    vstack = vstack,
                    cstack = cstack,
                    store = store,
                    execution = execution,
                    state = state,
                    postReturnSlot = target.postReturnSlot,
                    arguments = coreResults,
                    argumentCount = resultCount,
                    results = scratch.callSlots(0),
                )
            }

            state.states.mayLeave[targetOwner] = previousTargetMayLeave
            state.states.mayLeave[owner] = false
            repeat(resultCount) { index -> vstack.setFrameSlot(index, adaptedResults[index]) }
            state.states.mayLeave[owner] = true
            null
        } catch (exception: CanonicalInvocationException) {
            state.states.mayLeave[targetOwner] = previousTargetMayLeave
            state.states.mayLeave[owner] = true
            exception.error
        } catch (exception: InvocationException) {
            state.states.mayLeave[targetOwner] = previousTargetMayLeave
            state.states.mayLeave[owner] = true
            ComponentInvocationError.CoreTrap(exception.error)
        } catch (exception: ComponentCallScopeException) {
            state.states.mayLeave[targetOwner] = previousTargetMayLeave
            state.states.mayLeave[owner] = true
            exception.error
        } finally {
            cleanupError = exitComponentCall(componentStore, scope)
        }
        val error = cleanupError ?: outcome
        if (error != null) state.states.poisoned[targetOwner] = true
        error?.asCoreTrap()
    }
}
