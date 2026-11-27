@file:JsQualifier("WebAssembly")

package io.github.charlietap.chasm.vm

import org.khronos.webgl.ArrayBuffer
import kotlin.js.JsName

@JsName("Module")
internal external class WasmModule(buffer: ArrayBuffer)

@JsName("Instance")
internal external class WasmInstance(module: WasmModule, imports: dynamic = definedExternally) {
    val exports: dynamic
}

@JsName("Function")
internal external class WasmFunction(descriptor: Any? = definedExternally, impl: Any? = definedExternally)

@JsName("Global")
internal external class WasmGlobal {
    var value: dynamic

    companion object {
        fun type(global: WasmGlobal): WasmGlobalType
    }
}

internal external interface WasmGlobalType {
    val value: String
    val mutable: Boolean
}

@JsName("Memory")
internal external class WasmMemory {
    val buffer: ArrayBuffer

    companion object {
        fun type(memory: WasmMemory): WasmMemoryType
    }
}

internal external interface WasmMemoryType {
    val shared: Boolean?
    val minimum: Int
    val maximum: Int?
}

@JsName("Table")
internal external class WasmTable {
    companion object {
        fun type(table: WasmTable): WasmTableType
    }
}

internal external interface WasmTableType {
    val element: String
    val minimum: Int
    val maximum: Int?
}

@JsName("Tag")
internal external class WasmTag(descriptor: Any? = definedExternally) {
    companion object {
        fun type(tag: WasmTag): WasmTagType
    }
}

internal external interface WasmTagType {
    val parameters: Array<String>?
}
