package io.github.charlietap.chasm.runtime.exception

/** Payload-free control transfer used when a host function raises a Wasm exception. */
class HostRaisedWasmException private constructor() : Exception() {

    companion object {
        val instance = HostRaisedWasmException()
    }
}
