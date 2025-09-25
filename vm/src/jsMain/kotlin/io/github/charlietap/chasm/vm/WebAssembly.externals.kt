@file:JsQualifier("WebAssembly")

package io.github.charlietap.chasm.vm

import org.khronos.webgl.ArrayBuffer
import kotlin.js.JsName

@JsName("Module")
external class WasmModule(buffer: ArrayBuffer)

@JsName("Instance")
external class WasmInstance(module: WasmModule, imports: dynamic = definedExternally) {
    val exports: dynamic
}

@JsName("Function")
external class WasmFunction(descriptor: Any? = definedExternally, impl: Any? = definedExternally) {
    companion object {
        fun type(fn: dynamic): WasmFunctionType
    }
}

external interface WasmFunctionType {
    val parameters: Array<String>?
    val results: Array<String>?
}

@JsName("Global")
external class WasmGlobal {
    var value: dynamic

    companion object {
        fun type(global: WasmGlobal): WasmGlobalType
    }
}

external interface WasmGlobalType {
    val value: String
    val mutable: Boolean
}

@JsName("Memory")
external class WasmMemory {
    val buffer: ArrayBuffer

    companion object {
        fun type(memory: WasmMemory): WasmMemoryType
    }
}

external interface WasmMemoryType {
    val shared: Boolean?
    val minimum: Int
    val maximum: Int?
}

@JsName("Table")
external class WasmTable {
    companion object {
        fun type(table: WasmTable): WasmTableType
    }
}

external interface WasmTableType {
    val element: String
    val minimum: Int
    val maximum: Int?
}

@JsName("Tag")
external class WasmTag(descriptor: Any? = definedExternally) {
    companion object {
        fun type(tag: WasmTag): WasmTagType
    }
}

external interface WasmTagType {
    val parameters: Array<String>?
}
