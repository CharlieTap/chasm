package io.github.charlietap.chasm.executor.invoker.component.canonical

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.NestedFunctionInvoker
import io.github.charlietap.chasm.executor.invoker.RawFunctionInvoker
import io.github.charlietap.chasm.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalStringEncoding
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.runtime.component.info.ComponentRuntimeInfo
import io.github.charlietap.chasm.runtime.component.instance.ComponentRuntimeState
import io.github.charlietap.chasm.runtime.component.store.ComponentCallScope
import io.github.charlietap.chasm.runtime.component.store.ComponentCallScratch
import io.github.charlietap.chasm.runtime.component.store.ComponentStore
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal class CanonicalCallContext(
    val config: RuntimeConfig,
    val store: Store,
    val componentStore: ComponentStore,
    val root: ComponentRootAddress,
    val owner: RuntimeComponentInstanceIndex,
    val runtimeInfo: ComponentRuntimeInfo,
    val state: ComponentRuntimeState,
    val encoding: CanonicalStringEncoding,
    val memorySlot: Int,
    val reallocSlot: Int,
    val scratch: ComponentCallScratch,
    val scope: ComponentCallScope,
    val coreInvoker: RawFunctionInvoker,
) {

    fun memory(): MemoryInstance {
        val address = state.memories.runtimeAddress(memorySlot, "memory")
        return store.memory(Address.Memory(address))
    }

    fun realloc(
        alignment: Int,
        size: Int,
    ): Int {
        if (alignment <= 0 || alignment and (alignment - 1) != 0) {
            invalidValue("canonical realloc alignment must be a positive power of two")
        }
        if (size < 0) invalidValue("canonical realloc size is out of range")

        val address = state.reallocs.runtimeAddress(reallocSlot, "realloc")
        val arguments = scratch.callSlots(4)
        arguments[0] = 0L
        arguments[1] = 0L
        arguments[2] = alignment.toLong()
        arguments[3] = size.toLong()
        val resultCount = coreInvoker(
            config,
            store,
            state.adapterInstance,
            Address.Function(address),
            arguments,
            4,
            arguments,
        ).componentResult()
        if (resultCount != 1) invalidValue("canonical realloc must return one pointer")
        val pointer = arguments[0].toInt()
        if (pointer < 0 || arguments[0] != pointer.toLong()) invalidValue("canonical realloc returned an invalid memory32 pointer")
        if (pointer and (alignment - 1) != 0) invalidValue("canonical realloc returned a misaligned pointer")
        PessimisticBoundsChecker(pointer, size, memory().size) { Unit }
        return pointer
    }

    fun invokeCore(
        functionSlot: Int,
        arguments: LongArray,
        argumentCount: Int,
        results: LongArray,
    ): Int = CanonicalCoreFunctionInvoker(
        config = config,
        store = store,
        state = state,
        functionSlot = functionSlot,
        arguments = arguments,
        argumentCount = argumentCount,
        results = results,
        coreInvoker = coreInvoker,
    )

    fun invokePostReturn(
        postReturnSlot: Int,
        arguments: LongArray,
        argumentCount: Int,
    ) = CanonicalPostReturnInvoker(
        config = config,
        store = store,
        state = state,
        postReturnSlot = postReturnSlot,
        arguments = arguments,
        argumentCount = argumentCount,
        results = scratch.callSlots(0),
        coreInvoker = coreInvoker,
    )
}

internal fun CanonicalCoreFunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    state: ComponentRuntimeState,
    functionSlot: Int,
    arguments: LongArray,
    argumentCount: Int,
    results: LongArray,
) = CanonicalCoreFunctionInvoker(
    config = config,
    store = store,
    state = state,
    functionSlot = functionSlot,
    arguments = arguments,
    argumentCount = argumentCount,
    results = results,
    coreInvoker = ::RawFunctionInvoker,
)

internal fun CanonicalCoreFunctionInvoker(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    execution: ExecutionContext,
    state: ComponentRuntimeState,
    functionSlot: Int,
    arguments: LongArray,
    argumentCount: Int,
    results: LongArray,
): Int {
    val address = state.coreFunctions.runtimeAddress(functionSlot, "core function")
    return NestedFunctionInvoker(
        vstack,
        cstack,
        store,
        execution,
        state.adapterInstance,
        Address.Function(address),
        arguments,
        argumentCount,
        results,
    )
}

internal fun CanonicalCoreFunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    state: ComponentRuntimeState,
    functionSlot: Int,
    arguments: LongArray,
    argumentCount: Int,
    results: LongArray,
    coreInvoker: RawFunctionInvoker,
): Int {
    val address = state.coreFunctions.runtimeAddress(functionSlot, "core function")
    return coreInvoker(
        config,
        store,
        state.adapterInstance,
        Address.Function(address),
        arguments,
        argumentCount,
        results,
    ).componentResult()
}

internal fun CanonicalPostReturnInvoker(
    config: RuntimeConfig,
    store: Store,
    state: ComponentRuntimeState,
    postReturnSlot: Int,
    arguments: LongArray,
    argumentCount: Int,
    results: LongArray,
) = CanonicalPostReturnInvoker(
    config = config,
    store = store,
    state = state,
    postReturnSlot = postReturnSlot,
    arguments = arguments,
    argumentCount = argumentCount,
    results = results,
    coreInvoker = ::RawFunctionInvoker,
)

internal fun CanonicalPostReturnInvoker(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    execution: ExecutionContext,
    state: ComponentRuntimeState,
    postReturnSlot: Int,
    arguments: LongArray,
    argumentCount: Int,
    results: LongArray,
) {
    val address = state.postReturns.runtimeAddress(postReturnSlot, "post-return")
    val resultCount = NestedFunctionInvoker(
        vstack,
        cstack,
        store,
        execution,
        state.adapterInstance,
        Address.Function(address),
        arguments,
        argumentCount,
        results,
    )
    if (resultCount != 0) invalidValue("canonical post-return must not return values")
}

internal fun CanonicalPostReturnInvoker(
    config: RuntimeConfig,
    store: Store,
    state: ComponentRuntimeState,
    postReturnSlot: Int,
    arguments: LongArray,
    argumentCount: Int,
    results: LongArray,
    coreInvoker: RawFunctionInvoker,
) {
    val address = state.postReturns.runtimeAddress(postReturnSlot, "post-return")
    val resultCount = coreInvoker(
        config,
        store,
        state.adapterInstance,
        Address.Function(address),
        arguments,
        argumentCount,
        results,
    ).componentResult()
    if (resultCount != 0) invalidValue("canonical post-return must not return values")
}

internal class CanonicalInvocationException(
    val error: ComponentInvocationError,
) : Exception()

internal fun invalidValue(reason: String): Nothing = throw CanonicalInvocationException(
    ComponentInvocationError.InvalidCanonicalValue(reason),
)

private fun IntArray.runtimeAddress(
    slot: Int,
    dependency: String,
): Int {
    if (slot < 0) missingDependency(dependency)
    return getOrNull(slot)?.takeIf { address -> address >= 0 } ?: missingDependency(dependency)
}

private fun <T> com.github.michaelbull.result.Result<T, io.github.charlietap.chasm.runtime.error.InvocationError>.componentResult(): T =
    fold(
        success = { it },
        failure = { error ->
            throw CanonicalInvocationException(ComponentInvocationError.CoreTrap(error))
        },
    )

private fun missingDependency(dependency: String): Nothing = throw CanonicalInvocationException(
    ComponentInvocationError.MissingCanonicalDependency(dependency),
)
