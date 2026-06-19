@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.charlietap.chasm.vm

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.js

internal actual class WebValue internal constructor(internal val value: JsAny?)

internal actual class WebObject internal constructor(internal val value: JsAny)

internal actual class WebArray internal constructor(internal val value: JsAny)

internal actual class WebByteView internal constructor(internal val value: JsAny)

internal actual fun webStoreReference(): StoreReference = Unit

internal actual fun <T> webCatch(block: () -> T): WasmVirtualMachine.Result<T> =
    try {
        WasmVirtualMachine.Result.Ok(block())
    } catch (error: Throwable) {
        WasmVirtualMachine.Result.Error(error.message ?: error.toString())
    }

internal actual fun webUndefined(): WebValue = WebValue(wasmUndefined())

private fun WebValue.requiredValue(): JsAny {
    return value ?: throw IllegalArgumentException("Expected JavaScript value")
}

internal actual fun webIsNullOrUndefined(value: WebValue?): Boolean {
    return value == null || wasmIsNullOrUndefined(value.value)
}

internal actual fun webNewObject(): WebObject = WebObject(wasmNewObject())

internal actual fun webGetObject(
    obj: WebObject,
    name: String,
): WebObject? {
    val value = wasmGet(obj.value, name)
    return if (wasmIsNullOrUndefined(value)) null else WebObject(value ?: wasmNewObject())
}

internal actual fun webGetValue(
    obj: WebObject,
    name: String,
): WebValue? {
    val value = wasmGet(obj.value, name)
    return if (wasmIsNullOrUndefined(value)) null else WebValue(value)
}

internal actual fun webSetObject(
    obj: WebObject,
    name: String,
    value: WebObject,
) {
    wasmSet(obj.value, name, value.value)
}

internal actual fun webSetValue(
    obj: WebObject,
    name: String,
    value: WebValue,
) {
    wasmSet(obj.value, name, value.value)
}

internal actual fun webArrayOf(values: List<WebValue>): WebArray {
    val array = wasmNewArray()
    for (value in values) {
        wasmArrayPush(array, value.value)
    }
    return WebArray(array)
}

internal actual fun webArrayValue(array: WebArray): WebValue = WebValue(array.value)

internal actual fun webValueAsArray(value: WebValue): WebArray {
    return WebArray(value.value ?: wasmNewArray())
}

internal actual fun webArrayLength(array: WebArray): Int = wasmArrayLength(array.value)

internal actual fun webArrayGet(
    array: WebArray,
    index: Int,
): WebValue = WebValue(wasmArrayGet(array.value, index))

internal actual fun webIsArray(value: WebValue): Boolean = wasmIsArray(value.value)

internal actual fun webModule(binary: ByteArray): ModuleReference {
    val array = wasmNewUint8Array(binary.size)
    for (index in binary.indices) {
        wasmSetByte(array, index, binary[index].toInt() and 0xFF)
    }
    return ModuleReference(wasmNewModule(wasmBuffer(array)))
}

internal actual fun webInstance(
    module: ModuleReference,
    imports: WebObject,
): InstanceReference {
    return InstanceReference(wasmNewInstance(module.value, imports.value))
}

internal actual fun webExports(instance: InstanceReference): WebObject {
    return WebObject(wasmExports(instance.value))
}

internal actual fun webFunctionReference(value: WebValue): FunctionReference {
    return FunctionReference(value.requiredValue())
}

internal actual fun webGlobalReference(value: WebValue): GlobalReference {
    return GlobalReference(value.requiredValue())
}

internal actual fun webMemoryReference(value: WebValue): MemoryReference {
    return MemoryReference(value.requiredValue())
}

internal actual fun webTableReference(value: WebValue): TableReference {
    return TableReference(value.requiredValue())
}

internal actual fun webFunctionExternalAddressValue(address: FunctionExternalAddress): WebValue {
    return WebValue(address.value)
}

internal actual fun webGlobalExternalAddressValue(address: GlobalExternalAddress): WebValue {
    return WebValue(address.value)
}

internal actual fun webMemoryExternalAddressValue(address: MemoryExternalAddress): WebValue {
    return WebValue(address.value)
}

internal actual fun webTableExternalAddressValue(address: TableExternalAddress): WebValue {
    return WebValue(address.value)
}

internal actual fun webTagExternalAddressValue(address: TagExternalAddress): WebValue {
    return WebValue(address.value)
}

internal actual fun webIsFunction(value: WebValue): Boolean = wasmIsFunction(value.value)

internal actual fun webIsGlobal(value: WebValue): Boolean = wasmIsGlobal(value.value)

internal actual fun webIsMemory(value: WebValue): Boolean = wasmIsMemory(value.value)

internal actual fun webIsTable(value: WebValue): Boolean = wasmIsTable(value.value)

internal actual fun webApply(
    function: WebValue,
    receiver: WebValue,
    args: WebArray,
): WebValue? {
    val result = wasmApply(function.value ?: return null, receiver.value, args.value)
    return if (wasmIsNullOrUndefined(result)) null else WebValue(result)
}

internal actual fun webGlobalValue(global: GlobalReference): WebValue {
    return WebValue(wasmGlobalValue(global.value))
}

internal actual fun webSetGlobalValue(
    global: GlobalReference,
    value: WebValue,
) {
    wasmSetGlobalValue(global.value, value.value)
}

internal actual fun webMemoryView(
    memory: MemoryReference,
    pointer: Int,
    length: Int,
): WebByteView {
    return WebByteView(wasmMemoryView(memory.value, pointer, length))
}

internal actual fun webFullMemoryView(memory: MemoryReference): WebByteView {
    return WebByteView(wasmFullMemoryView(memory.value))
}

internal actual fun webByteViewLength(view: WebByteView): Int = wasmByteViewLength(view.value)

internal actual fun webReadByte(
    view: WebByteView,
    index: Int,
): Int = wasmReadByte(view.value, index)

internal actual fun webWriteByte(
    view: WebByteView,
    index: Int,
    value: Int,
) {
    wasmSetByte(view.value, index, value and 0xFF)
}

@Suppress("UNUSED_PARAMETER")
internal actual fun webHostFunction(
    paramTypes: List<String>,
    resultTypes: List<String>,
    callback: (List<WebValue>) -> WebValue?,
): FunctionExternalAddress {
    val implementation = when (paramTypes.size) {
        0 -> wasmHostFunction0 {
            callback(emptyList())?.value
        }
        1 -> wasmHostFunction1 { a ->
            callback(listOf(WebValue(a)))?.value
        }
        2 -> wasmHostFunction2 { a, b ->
            callback(listOf(WebValue(a), WebValue(b)))?.value
        }
        3 -> wasmHostFunction3 { a, b, c ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c)))?.value
        }
        4 -> wasmHostFunction4 { a, b, c, d ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d)))?.value
        }
        5 -> wasmHostFunction5 { a, b, c, d, e ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e)))?.value
        }
        6 -> wasmHostFunction6 { a, b, c, d, e, f ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f)))?.value
        }
        7 -> wasmHostFunction7 { a, b, c, d, e, f, g ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g)))?.value
        }
        8 -> wasmHostFunction8 { a, b, c, d, e, f, g, h ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h)))?.value
        }
        9 -> wasmHostFunction9 { a, b, c, d, e, f, g, h, i ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h), WebValue(i)))?.value
        }
        10 -> wasmHostFunction10 { a, b, c, d, e, f, g, h, i, j ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h), WebValue(i), WebValue(j)))?.value
        }
        11 -> wasmHostFunction11 { a, b, c, d, e, f, g, h, i, j, k ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h), WebValue(i), WebValue(j), WebValue(k)))?.value
        }
        12 -> wasmHostFunction12 { a, b, c, d, e, f, g, h, i, j, k, l ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h), WebValue(i), WebValue(j), WebValue(k), WebValue(l)))?.value
        }
        13 -> wasmHostFunction13 { a, b, c, d, e, f, g, h, i, j, k, l, m ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h), WebValue(i), WebValue(j), WebValue(k), WebValue(l), WebValue(m)))?.value
        }
        14 -> wasmHostFunction14 { a, b, c, d, e, f, g, h, i, j, k, l, m, n ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h), WebValue(i), WebValue(j), WebValue(k), WebValue(l), WebValue(m), WebValue(n)))?.value
        }
        15 -> wasmHostFunction15 { a, b, c, d, e, f, g, h, i, j, k, l, m, n, o ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h), WebValue(i), WebValue(j), WebValue(k), WebValue(l), WebValue(m), WebValue(n), WebValue(o)))?.value
        }
        else -> throw IllegalArgumentException("Failed to allocate host function with more than 15 parameters")
    }

    return FunctionExternalAddress(implementation)
}

internal actual fun webI32(value: Int): WebValue = WebValue(wasmI32(value))

internal actual fun webI64(value: Long): WebValue = WebValue(wasmI64(value))

internal actual fun webF32(value: Float): WebValue = WebValue(wasmF32(value))

internal actual fun webF64(value: Double): WebValue = WebValue(wasmF64(value))

internal actual fun webToI32(value: WebValue): Int = wasmToI32(value.value)

internal actual fun webToI64(value: WebValue): Long = wasmToI64(value.value)

internal actual fun webToF32(value: WebValue): Float = wasmToF32(value.value)

internal actual fun webToF64(value: WebValue): Double = wasmToF64(value.value)

internal actual fun webToDouble(value: WebValue): Double = wasmToDouble(value.value)

internal actual fun webTypeOf(value: WebValue): String = wasmTypeOf(value.value)

internal actual fun webFround(value: WebValue): Double = wasmFround(value.value)

private fun wasmUndefined(): JsAny? = js("undefined")

private fun wasmIsNullOrUndefined(value: JsAny?): Boolean = js("value == null")

private fun wasmNewObject(): JsAny = js("({})")

private fun wasmGet(
    obj: JsAny,
    name: String,
): JsAny? = js("obj[name]")

private fun wasmSet(
    obj: JsAny,
    name: String,
    value: JsAny?,
): Unit = js("{ obj[name] = value; }")

private fun wasmNewArray(): JsAny = js("[]")

private fun wasmArrayPush(
    array: JsAny,
    value: JsAny?,
): Unit = js("{ array.push(value); }")

private fun wasmArrayLength(array: JsAny): Int = js("array.length")

private fun wasmArrayGet(
    array: JsAny,
    index: Int,
): JsAny? = js("array[index]")

private fun wasmIsArray(value: JsAny?): Boolean = js("Array.isArray(value)")

private fun wasmNewUint8Array(size: Int): JsAny = js("new Uint8Array(size)")

private fun wasmBuffer(array: JsAny): JsAny = js("array.buffer")

private fun wasmNewModule(buffer: JsAny): JsAny = js("new WebAssembly.Module(buffer)")

private fun wasmNewInstance(
    module: JsAny,
    imports: JsAny,
): JsAny = js("new WebAssembly.Instance(module, imports)")

private fun wasmExports(instance: JsAny): JsAny = js("instance.exports")

private fun wasmIsFunction(value: JsAny?): Boolean = js(
    "typeof value === 'function'",
)

private fun wasmIsGlobal(value: JsAny?): Boolean = js("value instanceof WebAssembly.Global")

private fun wasmIsMemory(value: JsAny?): Boolean = js("value instanceof WebAssembly.Memory")

private fun wasmIsTable(value: JsAny?): Boolean = js("value instanceof WebAssembly.Table")

private fun wasmApply(
    fn: JsAny,
    receiver: JsAny?,
    args: JsAny,
): JsAny? = js("fn.apply(receiver, args)")

private fun wasmGlobalValue(global: JsAny): JsAny? = js("global.value")

private fun wasmSetGlobalValue(
    global: JsAny,
    value: JsAny?,
): Unit = js("{ global.value = value; }")

private fun wasmMemoryView(
    memory: JsAny,
    pointer: Int,
    length: Int,
): JsAny = js("new Uint8Array(memory.buffer, pointer, length)")

private fun wasmFullMemoryView(memory: JsAny): JsAny = js("new Uint8Array(memory.buffer)")

private fun wasmByteViewLength(view: JsAny): Int = js("view.length")

private fun wasmReadByte(
    view: JsAny,
    index: Int,
): Int = js("view[index]")

private fun wasmSetByte(
    view: JsAny,
    index: Int,
    value: Int,
): Unit = js("{ view[index] = value; }")

private fun wasmHostFunction0(callback: () -> JsAny?): JsAny = js("(function() { return callback(); })")

private fun wasmHostFunction1(callback: (JsAny?) -> JsAny?): JsAny = js("(function(a) { return callback(a); })")

private fun wasmHostFunction2(callback: (JsAny?, JsAny?) -> JsAny?): JsAny = js("(function(a, b) { return callback(a, b); })")

private fun wasmHostFunction3(callback: (JsAny?, JsAny?, JsAny?) -> JsAny?): JsAny = js("(function(a, b, c) { return callback(a, b, c); })")

private fun wasmHostFunction4(callback: (JsAny?, JsAny?, JsAny?, JsAny?) -> JsAny?): JsAny = js("(function(a, b, c, d) { return callback(a, b, c, d); })")

private fun wasmHostFunction5(callback: (JsAny?, JsAny?, JsAny?, JsAny?, JsAny?) -> JsAny?): JsAny = js("(function(a, b, c, d, e) { return callback(a, b, c, d, e); })")

private fun wasmHostFunction6(callback: (JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?) -> JsAny?): JsAny = js("(function(a, b, c, d, e, f) { return callback(a, b, c, d, e, f); })")

private fun wasmHostFunction7(callback: (JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?) -> JsAny?): JsAny = js("(function(a, b, c, d, e, f, g) { return callback(a, b, c, d, e, f, g); })")

private fun wasmHostFunction8(callback: (JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?) -> JsAny?): JsAny = js("(function(a, b, c, d, e, f, g, h) { return callback(a, b, c, d, e, f, g, h); })")

private fun wasmHostFunction9(callback: (JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?) -> JsAny?): JsAny = js("(function(a, b, c, d, e, f, g, h, i) { return callback(a, b, c, d, e, f, g, h, i); })")

private fun wasmHostFunction10(callback: (JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?) -> JsAny?): JsAny = js("(function(a, b, c, d, e, f, g, h, i, j) { return callback(a, b, c, d, e, f, g, h, i, j); })")

private fun wasmHostFunction11(callback: (JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?) -> JsAny?): JsAny = js("(function(a, b, c, d, e, f, g, h, i, j, k) { return callback(a, b, c, d, e, f, g, h, i, j, k); })")

private fun wasmHostFunction12(callback: (JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?) -> JsAny?): JsAny = js("(function(a, b, c, d, e, f, g, h, i, j, k, l) { return callback(a, b, c, d, e, f, g, h, i, j, k, l); })")

private fun wasmHostFunction13(callback: (JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?) -> JsAny?): JsAny = js("(function(a, b, c, d, e, f, g, h, i, j, k, l, m) { return callback(a, b, c, d, e, f, g, h, i, j, k, l, m); })")

private fun wasmHostFunction14(callback: (JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?) -> JsAny?): JsAny = js("(function(a, b, c, d, e, f, g, h, i, j, k, l, m, n) { return callback(a, b, c, d, e, f, g, h, i, j, k, l, m, n); })")

private fun wasmHostFunction15(callback: (JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?, JsAny?) -> JsAny?): JsAny = js("(function(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) { return callback(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o); })")

private fun wasmI32(value: Int): JsAny = js("value")

private fun wasmI64(value: Long): JsAny = js("value")

private fun wasmF32(value: Float): JsAny = js("value")

private fun wasmF64(value: Double): JsAny = js("value")

private fun wasmToI32(value: JsAny?): Int = js("value")

private fun wasmToI64(value: JsAny?): Long = js("value")

private fun wasmToF32(value: JsAny?): Float = js("Number(Math.fround(value).toPrecision(6))")

private fun wasmToF64(value: JsAny?): Double = js("value")

private fun wasmToDouble(value: JsAny?): Double = js("value")

private fun wasmTypeOf(value: JsAny?): String = js("typeof value")

private fun wasmFround(value: JsAny?): Double = js("Math.fround(value)")
