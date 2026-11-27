package io.github.charlietap.chasm.runtime.host

import io.github.charlietap.chasm.InternalChasmApi

/**
 * `HostGlobal` is an API for reading and changing a WebAssembly global's raw
 * value inside host functions.
 */
@InternalChasmApi
interface HostGlobal {

    var rawValue: Long
}
