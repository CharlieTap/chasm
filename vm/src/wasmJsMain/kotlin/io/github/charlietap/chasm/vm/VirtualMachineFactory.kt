@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.charlietap.chasm.vm

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

actual typealias StoreReference = Unit

internal actual class ModuleReference internal constructor(internal val value: JsAny)

internal actual class InstanceReference internal constructor(internal val value: JsAny)

internal actual class FunctionReference internal constructor(internal val value: JsAny)

internal actual class GlobalReference internal constructor(internal val value: JsAny)

internal actual class MemoryReference internal constructor(internal val value: JsAny)

internal actual class TableReference internal constructor(internal val value: JsAny)

actual fun virtualMachineFactory(): WasmVirtualMachine {
    return WebVirtualMachine
}
