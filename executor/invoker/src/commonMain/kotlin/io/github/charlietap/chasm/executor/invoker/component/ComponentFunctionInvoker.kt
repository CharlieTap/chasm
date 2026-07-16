package io.github.charlietap.chasm.executor.invoker.component

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.RawFunctionInvoker
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalCallContext
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalComponentValueValidator
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalFlatLifter
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalFlatLowerValidator
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalFlatLowerer
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalInvocationException
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalMemoryAllocator
import io.github.charlietap.chasm.executor.invoker.component.canonical.CanonicalMemoryLifter
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalStringEncoding
import io.github.charlietap.chasm.runtime.component.canonical.LiftParameterPassing
import io.github.charlietap.chasm.runtime.component.canonical.LiftResultPassing
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLiftPlan
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.function.PreparedComponentFunction
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction
import io.github.charlietap.chasm.runtime.component.index.PreparedComponentFunctionIndex
import io.github.charlietap.chasm.runtime.component.info.ComponentRuntimeInfo
import io.github.charlietap.chasm.runtime.component.instance.ComponentRuntimeState
import io.github.charlietap.chasm.runtime.component.store.ComponentCallScope
import io.github.charlietap.chasm.runtime.component.store.ComponentStore
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.store.identity
import io.github.charlietap.chasm.runtime.value.component.ComponentValue

typealias ComponentFunctionInvoker = (
    RuntimeConfig,
    Store,
    ComponentStore,
    ComponentRootAddress,
    PreparedComponentFunctionIndex,
    List<ComponentValue>,
) -> Result<List<ComponentValue>, ComponentInvocationError>

fun ComponentFunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    componentStore: ComponentStore,
    root: ComponentRootAddress,
    function: PreparedComponentFunctionIndex,
    arguments: List<ComponentValue>,
): Result<List<ComponentValue>, ComponentInvocationError> {
    val instance = componentStore.liveRoot(root).fold(
        success = { it },
        failure = { return Err(it) },
    )
    if (instance.state.deallocated) return Err(ComponentInvocationError.InstanceDeallocated)
    val prepared = instance.runtimeInfo.functions.getOrNull(function.index)
        ?: return Err(ComponentInvocationError.FunctionNotFound(function))
    val owner = prepared.owner
    val scope = componentStore.enterCall(root, caller = null, callee = owner)
    val cleanupError: ComponentInvocationError?
    val result = try {
        enterComponentInstance(instance.state.states, prepared.entryPolicy, scope)
        invokePreparedFunction(
            config = config,
            store = store,
            componentStore = componentStore,
            root = root,
            runtimeInfo = instance.runtimeInfo,
            state = instance.state,
            function = function,
            arguments = arguments,
            scope = scope,
            coreInvoker = ::RawFunctionInvoker,
        )
    } catch (exception: ComponentCallScopeException) {
        Err(exception.error)
    } finally {
        cleanupError = exitComponentCall(componentStore, scope)
    }
    if (cleanupError != null) instance.state.states.poisoned[owner.index] = true
    return cleanupError?.let(::Err) ?: result
}

internal fun invokePreparedFunction(
    config: RuntimeConfig,
    store: Store,
    componentStore: ComponentStore,
    root: ComponentRootAddress,
    runtimeInfo: ComponentRuntimeInfo,
    state: ComponentRuntimeState,
    function: PreparedComponentFunctionIndex,
    arguments: List<ComponentValue>,
    scope: ComponentCallScope,
    coreInvoker: RawFunctionInvoker,
): Result<List<ComponentValue>, ComponentInvocationError> {
    val prepared = runtimeInfo.functions.getOrNull(function.index)
        ?: return Err(ComponentInvocationError.FunctionNotFound(function))
    return when (prepared) {
        is PreparedComponentFunction.HostImport -> {
            val host = state.hostFunctions.getOrNull(prepared.importSlot)
                ?: return Err(ComponentInvocationError.MissingCanonicalDependency("host function"))
            when (host) {
                is RuntimeComponentHostFunction.Linked -> invokeLinkedComponentFunction(
                    store,
                    componentStore,
                    host,
                    arguments,
                )
                is RuntimeComponentHostFunction.Dynamic -> invokeDynamicHostFunction(
                    store,
                    root,
                    runtimeInfo,
                    prepared,
                    host,
                    arguments,
                    scope,
                )
                is RuntimeComponentHostFunction.Prepared -> invokePreparedHostFunction(
                    config,
                    store,
                    componentStore,
                    root,
                    runtimeInfo,
                    state,
                    prepared,
                    host,
                    arguments,
                    scope,
                    coreInvoker,
                )
            }
        }
        is PreparedComponentFunction.LiftedCore -> invokeLiftedCore(
            config,
            store,
            componentStore,
            root,
            runtimeInfo,
            state,
            prepared.liftPlan,
            arguments,
            scope,
            coreInvoker,
        )
    }
}

private fun invokeLinkedComponentFunction(
    store: Store,
    componentStore: ComponentStore,
    host: RuntimeComponentHostFunction.Linked,
    arguments: List<ComponentValue>,
): Result<List<ComponentValue>, ComponentInvocationError> {
    val instance = componentStore.liveRoot(host.root).fold(
        success = { it },
        failure = { return Err(it) },
    )
    return ComponentFunctionInvoker(
        config = instance.config,
        store = store,
        componentStore = componentStore,
        root = host.root,
        function = host.function,
        arguments = arguments,
    )
}

private fun invokeDynamicHostFunction(
    store: Store,
    root: ComponentRootAddress,
    runtimeInfo: ComponentRuntimeInfo,
    prepared: PreparedComponentFunction.HostImport,
    host: RuntimeComponentHostFunction.Dynamic,
    arguments: List<ComponentValue>,
    scope: ComponentCallScope,
): Result<List<ComponentValue>, ComponentInvocationError> = try {
    if (scope.isHostCaller) CanonicalComponentValueValidator(runtimeInfo, prepared.parameterTuple, arguments)
    host(scope.hostFunctionContext(store.identity(), root), arguments).fold(
        success = { results ->
            if (scope.isHostCaller) CanonicalComponentValueValidator(runtimeInfo, prepared.resultTuple, results)
            Ok(results)
        },
        failure = ::Err,
    )
} catch (exception: CanonicalInvocationException) {
    Err(exception.error)
}

private fun invokePreparedHostFunction(
    config: RuntimeConfig,
    store: Store,
    componentStore: ComponentStore,
    root: ComponentRootAddress,
    runtimeInfo: ComponentRuntimeInfo,
    state: ComponentRuntimeState,
    prepared: PreparedComponentFunction.HostImport,
    host: RuntimeComponentHostFunction.Prepared,
    arguments: List<ComponentValue>,
    scope: ComponentCallScope,
    coreInvoker: RawFunctionInvoker,
): Result<List<ComponentValue>, ComponentInvocationError> = try {
    if (!prepared.preparedHostCompatible) {
        return Err(ComponentInvocationError.InvalidCanonicalValue("prepared host function requires a memory-free type"))
    }
    val context = CanonicalCallContext(
        config = config,
        store = store,
        componentStore = componentStore,
        root = root,
        owner = prepared.owner,
        runtimeInfo = runtimeInfo,
        state = state,
        encoding = CanonicalStringEncoding.Utf8,
        memorySlot = ABSENT_CANONICAL_SLOT,
        reallocSlot = ABSENT_CANONICAL_SLOT,
        scratch = scope.scratch,
        scope = scope,
        coreInvoker = coreInvoker,
    )
    val flatArguments = scope.scratch.slots(prepared.parameterTuple.flatCount)
    val argumentCount = CanonicalFlatLowerer(context, prepared.parameterTuple, arguments, flatArguments)
    val flatResults = scope.scratch.callSlots(prepared.resultTuple.flatCount)
    val resultCount = host(
        scope.hostFunctionContext(store.identity(), root),
        flatArguments,
        argumentCount,
        flatResults,
    ).fold(
        success = { it },
        failure = { return Err(it) },
    )
    CanonicalFlatLowerValidator(runtimeInfo, prepared.resultTuple, flatResults, resultCount)
    Ok(CanonicalFlatLifter(context, prepared.resultTuple, flatResults))
} catch (exception: CanonicalInvocationException) {
    Err(exception.error)
}

private fun invokeLiftedCore(
    config: RuntimeConfig,
    store: Store,
    componentStore: ComponentStore,
    root: ComponentRootAddress,
    runtimeInfo: ComponentRuntimeInfo,
    state: ComponentRuntimeState,
    plan: LinearMemoryLiftPlan,
    arguments: List<ComponentValue>,
    scope: ComponentCallScope,
    coreInvoker: RawFunctionInvoker,
): Result<List<ComponentValue>, ComponentInvocationError> {
    val scratch = scope.scratch
    val context = CanonicalCallContext(
        config = config,
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
        coreInvoker = coreInvoker,
    )
    val owner = plan.optionOwner.index
    val previousMayLeave = state.states.mayLeave[owner]
    var guestResultPhase = false
    val result = try {
        state.states.mayLeave[owner] = false
        val coreArguments = scratch.slots(maxOf(plan.parameterTuple.flatCount, 1))
        val argumentCount = when (plan.parameterPassing) {
            LiftParameterPassing.Direct -> CanonicalFlatLowerer(
                context,
                plan.parameterTuple,
                arguments,
                coreArguments,
            )
            LiftParameterPassing.IndirectTuple -> {
                coreArguments[0] = CanonicalMemoryAllocator(context, plan.parameterTuple, arguments).toLong()
                1
            }
        }

        state.states.mayLeave[owner] = previousMayLeave
        val coreResults = scratch.callSlots(maxOf(plan.resultTuple.flatCount, 1))
        val resultCount = context.invokeCore(plan.coreFunctionSlot, coreArguments, argumentCount, coreResults)
        guestResultPhase = true
        val expectedResults = when (plan.resultPassing) {
            LiftResultPassing.Direct -> plan.resultTuple.flatCount
            LiftResultPassing.IndirectPointer -> 1
        }
        if (resultCount != expectedResults) {
            throw CanonicalInvocationException(
                ComponentInvocationError.InvalidCanonicalValue("core function returned the wrong canonical result count"),
            )
        }

        state.states.mayLeave[owner] = false
        val results = when (plan.resultPassing) {
            LiftResultPassing.Direct -> CanonicalFlatLifter(context, plan.resultTuple, coreResults)
            LiftResultPassing.IndirectPointer -> CanonicalMemoryLifter(
                context,
                plan.resultTuple,
                coreResults[0].toInt(),
            )
        }
        if (plan.postReturnSlot >= 0) {
            context.invokePostReturn(plan.postReturnSlot, coreResults, resultCount)
        }
        state.states.mayLeave[owner] = previousMayLeave
        Ok(results)
    } catch (exception: CanonicalInvocationException) {
        state.states.mayLeave[owner] = previousMayLeave
        if (guestResultPhase || exception.error is ComponentInvocationError.CoreTrap) {
            state.states.poisoned[owner] = true
        }
        return Err(exception.error)
    } catch (exception: InvocationException) {
        state.states.mayLeave[owner] = previousMayLeave
        state.states.poisoned[owner] = true
        return Err(ComponentInvocationError.CoreTrap(exception.error))
    }
    return result
}

internal fun ComponentInvocationError.asCoreTrap(): Nothing = throw InvocationException(
    InvocationError.ComponentFunctionError(toString()),
)

private const val ABSENT_CANONICAL_SLOT = -1
