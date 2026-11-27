package io.github.charlietap.chasm.runtime.exception

import io.github.charlietap.chasm.InternalChasmApi

/** Payload-free control transfer used when a host function raises a Wasm exception. */
@InternalChasmApi
class HostRaisedWasmException private constructor() : Exception() {

    companion object {
        val instance = HostRaisedWasmException()
    }
}
