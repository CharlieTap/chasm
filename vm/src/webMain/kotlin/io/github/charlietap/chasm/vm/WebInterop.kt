package io.github.charlietap.chasm.vm

internal expect class WebValue

internal expect class WebObject

internal expect class WebArray

internal expect class WebByteView

internal expect fun webStoreReference(): StoreReference

internal expect fun <T> webCatch(block: () -> T): WasmVirtualMachine.Result<T>

internal expect fun webUndefined(): WebValue

internal expect fun webIsNullOrUndefined(value: WebValue?): Boolean

internal expect fun webNewObject(): WebObject

internal expect fun webGetObject(
    obj: WebObject,
    name: String,
): WebObject?

internal expect fun webGetValue(
    obj: WebObject,
    name: String,
): WebValue?

internal expect fun webSetObject(
    obj: WebObject,
    name: String,
    value: WebObject,
)

internal expect fun webSetValue(
    obj: WebObject,
    name: String,
    value: WebValue,
)

internal expect fun webArrayOf(values: List<WebValue>): WebArray

internal expect fun webArrayValue(array: WebArray): WebValue

internal expect fun webValueAsArray(value: WebValue): WebArray

internal expect fun webArrayLength(array: WebArray): Int

internal expect fun webArrayGet(
    array: WebArray,
    index: Int,
): WebValue

internal expect fun webIsArray(value: WebValue): Boolean

internal expect fun webModule(binary: ByteArray): ModuleReference

internal expect fun webInstance(
    module: ModuleReference,
    imports: WebObject,
): InstanceReference

internal expect fun webExports(instance: InstanceReference): WebObject

internal expect fun webFunctionReference(value: WebValue): FunctionReference

internal expect fun webGlobalReference(value: WebValue): GlobalReference

internal expect fun webMemoryReference(value: WebValue): MemoryReference

internal expect fun webTableReference(value: WebValue): TableReference

internal expect fun webFunctionExternalAddressValue(address: FunctionExternalAddress): WebValue

internal expect fun webGlobalExternalAddressValue(address: GlobalExternalAddress): WebValue

internal expect fun webMemoryExternalAddressValue(address: MemoryExternalAddress): WebValue

internal expect fun webTableExternalAddressValue(address: TableExternalAddress): WebValue

internal expect fun webTagExternalAddressValue(address: TagExternalAddress): WebValue

internal expect fun webIsFunction(value: WebValue): Boolean

internal expect fun webIsGlobal(value: WebValue): Boolean

internal expect fun webIsMemory(value: WebValue): Boolean

internal expect fun webIsTable(value: WebValue): Boolean

internal expect fun webApply(
    function: WebValue,
    receiver: WebValue,
    args: WebArray,
): WebValue?

internal expect fun webGlobalValue(global: GlobalReference): WebValue

internal expect fun webSetGlobalValue(
    global: GlobalReference,
    value: WebValue,
)

internal expect fun webMemoryView(
    memory: MemoryReference,
    pointer: Int,
    length: Int,
): WebByteView

internal expect fun webFullMemoryView(memory: MemoryReference): WebByteView

internal expect fun webByteViewLength(view: WebByteView): Int

internal expect fun webReadByte(
    view: WebByteView,
    index: Int,
): Int

internal expect fun webWriteByte(
    view: WebByteView,
    index: Int,
    value: Int,
)

internal expect fun webHostFunction(
    paramTypes: List<String>,
    resultTypes: List<String>,
    callback: (List<WebValue>) -> WebValue?,
): FunctionExternalAddress

internal expect fun webI32(value: Int): WebValue

internal expect fun webI64(value: Long): WebValue

internal expect fun webF32(value: Float): WebValue

internal expect fun webF64(value: Double): WebValue

internal expect fun webToI32(value: WebValue): Int

internal expect fun webToI64(value: WebValue): Long

internal expect fun webToF32(value: WebValue): Float

internal expect fun webToF64(value: WebValue): Double

internal expect fun webToDouble(value: WebValue): Double

internal expect fun webTypeOf(value: WebValue): String

internal expect fun webFround(value: WebValue): Double
