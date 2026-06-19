@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.charlietap.chasm.vm

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

internal actual class FunctionExternalAddress internal constructor(internal val value: JsAny)

internal actual class GlobalExternalAddress internal constructor(internal val value: JsAny)

internal actual class MemoryExternalAddress internal constructor(internal val value: JsAny)

internal actual class TableExternalAddress internal constructor(internal val value: JsAny)

internal actual class TagExternalAddress internal constructor(internal val value: JsAny)
