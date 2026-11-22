package io.github.charlietap.chasm.runtime.host

/**
 * `HostGlobal` is an API for reading and changing a WebAssembly global's raw
 * value inside host functions.
 */
interface HostGlobal {

    var rawValue: Long
}
