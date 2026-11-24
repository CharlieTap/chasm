package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.InternalChasmApi

interface CompiledModuleStructs {

    @InternalChasmApi
    fun structLanes(): LongArray

    @InternalChasmApi
    fun newStruct(
        type: Int,
        rtt: Any,
    ): Any
}
