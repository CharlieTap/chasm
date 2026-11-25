package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.compiled.CompiledModule

internal class StoreModuleNames {

    private val holders = HashMap<String, NameHolder>()

    @Synchronized
    fun claim(payload: CompiledModulePayload): Boolean {

        val holder = holders.getOrPut(payload.resourceRoot) { NameHolder(payload.compiled) }
        if (holder.compiled !== payload.compiled) {
            return false
        }
        holder.uses++

        return true
    }

    @Synchronized
    fun release(payload: CompiledModulePayload) {

        val holder = holders.getValue(payload.resourceRoot)
        holder.uses--
        if (holder.uses == 0) {
            holders.remove(payload.resourceRoot)
        }
    }

    private class NameHolder(
        val compiled: CompiledModule,
    ) {
        var uses = 0
    }
}
