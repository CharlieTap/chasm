package io.github.charlietap.chasm.vm

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Creates a virtual machine that uses [dispatcher] for parallel decoding and
 * compilation when the platform supports it.
 *
 * JS and WasmJS use their platform WebAssembly implementation synchronously
 * behind the same suspending API.
 */
expect fun suspendingVirtualMachineFactory(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): SuspendingWasmVirtualMachine
