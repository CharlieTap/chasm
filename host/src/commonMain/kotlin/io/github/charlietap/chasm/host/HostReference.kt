package io.github.charlietap.chasm.host

/**
 * Chasm's raw, unboxed WebAssembly reference representation.
 *
 * ```
 * 63                     8 7                      0
 * +-----------------------+------------------------+
 * |        payload        |      type tag (8)      |
 * +-----------------------+------------------------+
 * ```
 *
 * The type tag is `reference and 0xff`; the payload is `reference shr 8`.
 *
 * - `1`: null; payload is the heap type
 * - `2`: i31; payload is the 31-bit value
 * - `3`: struct; payload is the heap address
 * - `4`: array; payload is the heap address
 * - `5`: function; payload is the store address
 * - `6`: host value; payload is the host-value slot
 * - `7`: exception; payload is the heap address
 * - `8`: extern; payload is another encoded reference
 *
 * Address payloads are only valid in the store that produced them.
 */
typealias HostReference = Long
