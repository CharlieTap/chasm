package io.github.charlietap.chasm.vm

import org.khronos.webgl.Uint8Array
import kotlin.js.unsafeCast

internal actual class WebValue internal constructor(internal val value: dynamic)

internal actual class WebObject internal constructor(internal val value: dynamic)

internal actual class WebArray internal constructor(internal val value: Array<dynamic>)

internal actual class WebByteView internal constructor(internal val value: Uint8Array)

internal actual fun webStoreReference(): StoreReference = Unit

internal actual fun <T> webCatch(block: () -> T): WasmVirtualMachine.Result<T> =
    try {
        WasmVirtualMachine.Result.Ok(block())
    } catch (error: dynamic) {
        WasmVirtualMachine.Result.Error(errorMessage(error))
    }

internal actual fun webUndefined(): WebValue = WebValue(UNDEFINED)

internal actual fun webIsNullOrUndefined(value: WebValue?): Boolean {
    return value == null || value.value == null || value.value === UNDEFINED
}

internal actual fun webNewObject(): WebObject = WebObject(newObj())

internal actual fun webGetObject(
    obj: WebObject,
    name: String,
): WebObject? {
    val value = obj.value[name]
    return if (value == null || value === UNDEFINED) null else WebObject(value)
}

internal actual fun webGetValue(
    obj: WebObject,
    name: String,
): WebValue? {
    val value = obj.value[name]
    return if (value == null || value === UNDEFINED) null else WebValue(value)
}

internal actual fun webSetObject(
    obj: WebObject,
    name: String,
    value: WebObject,
) {
    obj.value[name] = value.value
}

internal actual fun webSetValue(
    obj: WebObject,
    name: String,
    value: WebValue,
) {
    obj.value[name] = value.value
}

internal actual fun webArrayOf(values: List<WebValue>): WebArray {
    return WebArray(values.map { value -> value.value }.toTypedArray())
}

internal actual fun webArrayValue(array: WebArray): WebValue = WebValue(array.value)

internal actual fun webValueAsArray(value: WebValue): WebArray {
    return WebArray(value.value.unsafeCast<Array<dynamic>>())
}

internal actual fun webArrayLength(array: WebArray): Int = array.value.size

internal actual fun webArrayGet(
    array: WebArray,
    index: Int,
): WebValue = WebValue(array.value[index])

internal actual fun webIsArray(value: WebValue): Boolean = isArray(value.value)

internal actual fun webModule(binary: ByteArray): ModuleReference {
    return WasmModule(binary.toUint8Array().buffer)
}

internal actual fun webInstance(
    module: ModuleReference,
    imports: WebObject,
): InstanceReference {
    return WasmInstance(module, imports.value)
}

internal actual fun webExports(instance: InstanceReference): WebObject {
    return WebObject(instance.exports)
}

internal actual fun webFunctionReference(value: WebValue): FunctionReference {
    return value.value.unsafeCast<WasmFunction>()
}

internal actual fun webGlobalReference(value: WebValue): GlobalReference {
    return value.value.unsafeCast<WasmGlobal>()
}

internal actual fun webMemoryReference(value: WebValue): MemoryReference {
    return value.value.unsafeCast<WasmMemory>()
}

internal actual fun webTableReference(value: WebValue): TableReference {
    return value.value.unsafeCast<WasmTable>()
}

internal actual fun webFunctionExternalAddressValue(address: FunctionExternalAddress): WebValue {
    return WebValue(address.fn)
}

internal actual fun webGlobalExternalAddressValue(address: GlobalExternalAddress): WebValue {
    return WebValue(address)
}

internal actual fun webMemoryExternalAddressValue(address: MemoryExternalAddress): WebValue {
    return WebValue(address)
}

internal actual fun webTableExternalAddressValue(address: TableExternalAddress): WebValue {
    return WebValue(address)
}

internal actual fun webTagExternalAddressValue(address: TagExternalAddress): WebValue {
    return WebValue(address)
}

internal actual fun webIsFunction(value: WebValue): Boolean {
    return typeOf(value.value) == "function"
}

internal actual fun webIsGlobal(value: WebValue): Boolean {
    return isGlobalType(value.value)
}

internal actual fun webIsMemory(value: WebValue): Boolean {
    return isMemoryType(value.value)
}

internal actual fun webIsTable(value: WebValue): Boolean {
    return isTableType(value.value)
}

internal actual fun webApply(
    function: WebValue,
    receiver: WebValue,
    args: WebArray,
): WebValue? {
    val result = function.value.apply(receiver.value, args.value)
    return if (result == null || result === UNDEFINED) null else WebValue(result)
}

internal actual fun webGlobalValue(global: GlobalReference): WebValue {
    return WebValue(global.value)
}

internal actual fun webSetGlobalValue(
    global: GlobalReference,
    value: WebValue,
) {
    global.value = value.value
}

internal actual fun webMemoryView(
    memory: MemoryReference,
    pointer: Int,
    length: Int,
): WebByteView {
    return WebByteView(Uint8Array(memory.buffer, pointer, length))
}

internal actual fun webFullMemoryView(memory: MemoryReference): WebByteView {
    return WebByteView(Uint8Array(memory.buffer))
}

internal actual fun webByteViewLength(view: WebByteView): Int = view.value.length

internal actual fun webReadByte(
    view: WebByteView,
    index: Int,
): Int {
    return view.value.asDynamic()[index] as Int
}

internal actual fun webWriteByte(
    view: WebByteView,
    index: Int,
    value: Int,
) {
    view.value.asDynamic()[index] = value and 0xFF
}

@Suppress("UNUSED_PARAMETER")
internal actual fun webHostFunction(
    paramTypes: List<String>,
    resultTypes: List<String>,
    callback: (List<WebValue>) -> WebValue?,
): FunctionExternalAddress {
    val implementation = when (paramTypes.size) {
        0 -> (
            {
                callback(emptyList())?.value ?: UNDEFINED
            } as Any
        ).unsafeCast<dynamic>()
        1 -> { a: dynamic ->
            callback(listOf(WebValue(a)))?.value ?: UNDEFINED
        }
        2 -> { a: dynamic, b: dynamic ->
            callback(listOf(WebValue(a), WebValue(b)))?.value ?: UNDEFINED
        }
        3 -> { a: dynamic, b: dynamic, c: dynamic ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c)))?.value ?: UNDEFINED
        }
        4 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d)))?.value ?: UNDEFINED
        }
        5 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e)))?.value ?: UNDEFINED
        }
        6 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f)))?.value ?: UNDEFINED
        }
        7 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g)))?.value ?: UNDEFINED
        }
        8 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h)))?.value ?: UNDEFINED
        }
        9 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic, i: dynamic ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h), WebValue(i)))?.value ?: UNDEFINED
        }
        10 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic, i: dynamic, j: dynamic ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h), WebValue(i), WebValue(j)))?.value ?: UNDEFINED
        }
        11 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic, i: dynamic, j: dynamic, k: dynamic ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h), WebValue(i), WebValue(j), WebValue(k)))?.value ?: UNDEFINED
        }
        12 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic, i: dynamic, j: dynamic, k: dynamic, l: dynamic ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h), WebValue(i), WebValue(j), WebValue(k), WebValue(l)))?.value ?: UNDEFINED
        }
        13 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic, i: dynamic, j: dynamic, k: dynamic, l: dynamic, m: dynamic ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h), WebValue(i), WebValue(j), WebValue(k), WebValue(l), WebValue(m)))?.value ?: UNDEFINED
        }
        14 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic, i: dynamic, j: dynamic, k: dynamic, l: dynamic, m: dynamic, n: dynamic ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h), WebValue(i), WebValue(j), WebValue(k), WebValue(l), WebValue(m), WebValue(n)))?.value ?: UNDEFINED
        }
        15 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic, i: dynamic, j: dynamic, k: dynamic, l: dynamic, m: dynamic, n: dynamic, o: dynamic ->
            callback(listOf(WebValue(a), WebValue(b), WebValue(c), WebValue(d), WebValue(e), WebValue(f), WebValue(g), WebValue(h), WebValue(i), WebValue(j), WebValue(k), WebValue(l), WebValue(m), WebValue(n), WebValue(o)))?.value ?: UNDEFINED
        }
        else -> throw IllegalArgumentException("Failed to allocate host function with more than 15 parameters")
    }

    return JsFunction(implementation)
}

internal actual fun webI32(value: Int): WebValue = WebValue(value)

internal actual fun webI64(value: Long): WebValue = WebValue(longToBigInt(value))

internal actual fun webF32(value: Float): WebValue = WebValue(value)

internal actual fun webF64(value: Double): WebValue = WebValue(value)

internal actual fun webToI32(value: WebValue): Int = value.value.unsafeCast<Int>()

internal actual fun webToI64(value: WebValue): Long = bigIntToLong(value.value)

internal actual fun webToF32(value: WebValue): Float {
    return froundToPrettyDouble(value.value).toFloat()
}

internal actual fun webToF64(value: WebValue): Double = value.value.unsafeCast<Double>()

internal actual fun webToDouble(value: WebValue): Double = value.value.unsafeCast<Double>()

internal actual fun webTypeOf(value: WebValue): String = typeOf(value.value)

internal actual fun webFround(value: WebValue): Double = fround(value.value)

private fun ByteArray.toUint8Array(): Uint8Array {
    val array = Uint8Array(size)
    for (index in indices) {
        array.asDynamic()[index] = this[index].toInt() and 0xFF
    }
    return array
}

private fun errorMessage(error: dynamic): String {
    if (error == null || error === UNDEFINED) return "Unknown JS error"
    val message = error.message
    return when {
        message !== UNDEFINED && message != null -> message.toString()
        else -> error.toString()
    }
}

private fun froundToPrettyDouble(value: dynamic): Double {
    return js("Number(Math.fround(value).toPrecision(6))").unsafeCast<Double>()
}

private fun isGlobalType(value: dynamic): Boolean {
    return js("value instanceof WebAssembly.Global").unsafeCast<Boolean>()
}

private fun isMemoryType(value: dynamic): Boolean {
    return js("value instanceof WebAssembly.Memory").unsafeCast<Boolean>()
}

private fun isTableType(value: dynamic): Boolean {
    return js("value instanceof WebAssembly.Table").unsafeCast<Boolean>()
}
