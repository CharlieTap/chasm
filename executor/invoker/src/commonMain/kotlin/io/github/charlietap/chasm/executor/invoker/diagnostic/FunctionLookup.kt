package io.github.charlietap.chasm.executor.invoker.diagnostic

import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.store.Store

/** Chasm keeps no per-function code index, so this is built from the store after a failure. */
internal class FunctionLookup private constructor(
    private val entryIps: IntArray,
    private val endIps: IntArray,
    private val addresses: IntArray,
    private val functions: Array<FunctionInstance.WasmFunction>,
) {

    fun indexOf(ip: Int): Int {
        var low = 0
        var high = entryIps.lastIndex
        var owner = -1
        while (low <= high) {
            val middle = (low + high) ushr 1
            if (entryIps[middle] <= ip) {
                owner = middle
                low = middle + 1
            } else {
                high = middle - 1
            }
        }
        return if (owner >= 0 && ip < endIps[owner]) owner else -1
    }

    fun address(index: Int): Int = addresses[index]

    fun function(index: Int): FunctionInstance.WasmFunction = functions[index]

    fun entryIp(index: Int): Int = entryIps[index]

    /** Exclusive. */
    fun endIp(index: Int): Int = endIps[index]

    companion object {

        operator fun invoke(store: Store): FunctionLookup {
            val candidates = ArrayList<Int>()
            for (address in store.functions.indices) {
                val function = store.functions[address]
                if (function is FunctionInstance.WasmFunction && function.callStrategy.isInstalled) {
                    candidates.add(address)
                }
            }
            candidates.sortBy { address ->
                (store.functions[address] as FunctionInstance.WasmFunction).callStrategy.entryIp
            }

            // Instances sharing installed code keep the first address in store order.
            val unique = ArrayList<Int>(candidates.size)
            for (address in candidates) {
                val entryIp = (store.functions[address] as FunctionInstance.WasmFunction).callStrategy.entryIp
                val previous = unique.lastOrNull()
                if (previous == null || (store.functions[previous] as FunctionInstance.WasmFunction).callStrategy.entryIp != entryIp) {
                    unique.add(address)
                }
            }

            val programSize = store.program.size
            val entryIps = IntArray(unique.size) { index ->
                (store.functions[unique[index]] as FunctionInstance.WasmFunction).callStrategy.entryIp
            }
            val endIps = IntArray(unique.size) { index ->
                if (index + 1 < entryIps.size) entryIps[index + 1] else programSize
            }
            return FunctionLookup(
                entryIps = entryIps,
                endIps = endIps,
                addresses = IntArray(unique.size) { index -> unique[index] },
                functions = Array(unique.size) { index -> store.functions[unique[index]] as FunctionInstance.WasmFunction },
            )
        }
    }
}
