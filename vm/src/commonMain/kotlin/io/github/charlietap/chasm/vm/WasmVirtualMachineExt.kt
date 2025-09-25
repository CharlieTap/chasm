package io.github.charlietap.chasm.vm

import io.github.charlietap.chasm.vm.WasmVirtualMachine.Result.Error
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Result.Ok
import io.github.charlietap.chasm.vm.codegen.CodegenImport
import io.github.charlietap.chasm.vm.codegen.FunctionImport

inline fun <A, B> WasmVirtualMachine.Result<A>.map(
    transformation: (A) -> B,
): WasmVirtualMachine.Result<B> {
    return when (this) {
        is Ok -> Ok(transformation(this.value))
        is Error -> this
    }
}

inline fun <A, B> WasmVirtualMachine.Result<A>.flatMap(
    transformation: (A) -> WasmVirtualMachine.Result<B>,
): WasmVirtualMachine.Result<B> {
    return when (this) {
        is Ok -> transformation(this.value)
        is Error -> Error(this.message)
    }
}

inline fun <A, R> WasmVirtualMachine.Result<A>.fold(
    onSuccess: (A) -> R,
    onError: (String) -> R,
): R {
    return when (this) {
        is Ok -> onSuccess(this.value)
        is Error -> onError(this.message)
    }
}

inline fun <A> WasmVirtualMachine.Result<A>.getOrNull(): A? {
    return when (this) {
        is Ok -> this.value
        is Error -> null
    }
}

inline fun <A> WasmVirtualMachine.Result<A>.getOrElse(defaultValue: A): A {
    return when (this) {
        is Ok -> this.value
        is Error -> defaultValue
    }
}

inline fun <A> WasmVirtualMachine.Result<A>.onSuccess(action: (A) -> Unit): WasmVirtualMachine.Result<A> {
    if (this is Ok) action(this.value)
    return this
}

inline fun <A> WasmVirtualMachine.Result<A>.onError(action: (String) -> Unit): WasmVirtualMachine.Result<A> {
    if (this is Error) action(this.message)
    return this
}

inline fun <T> WasmVirtualMachine.Result<T>.expect(
    errorMessage: String = "Expectation failure",
): T {
    return when (this) {
        is Ok<T> -> this.value
        is Error -> throw IllegalStateException("$errorMessage: ${this.message}")
    }
}

inline fun WasmVirtualMachine.Result<WasmVirtualMachine.Value>.expectValue(
    errorMessage: String = "Expectation failure",
): WasmVirtualMachine.Value {
    return when (this) {
        is Ok<WasmVirtualMachine.Value> -> this.value
        is Error -> throw IllegalStateException("$errorMessage: ${this.message}")
    }
}

inline fun <reified T : WasmVirtualMachine.Value> WasmVirtualMachine.Result<WasmVirtualMachine.Value>.expectValueAs(
    errorMessage: String = "Expectation failure",
): T {
    return expectValue(errorMessage) as? T
        ?: throw IllegalStateException("$errorMessage: value is not of expected type")
}

inline fun WasmVirtualMachine.Result<WasmVirtualMachine.Value>.expectInt(
    errorMessage: String = "Expectation failure",
): Int {
    return expectValueAs<WasmVirtualMachine.Value.I32>(errorMessage).value
}

inline fun WasmVirtualMachine.Result<WasmVirtualMachine.Value>.expectLong(
    errorMessage: String = "Expectation failure",
): Long {
    return expectValueAs<WasmVirtualMachine.Value.I64>(errorMessage).value
}

inline fun WasmVirtualMachine.Result<WasmVirtualMachine.Value>.expectFloat(
    errorMessage: String = "Expectation failure",
): Float {
    return expectValueAs<WasmVirtualMachine.Value.F32>(errorMessage).value
}

inline fun WasmVirtualMachine.Result<WasmVirtualMachine.Value>.expectDouble(
    errorMessage: String = "Expectation failure",
): Double {
    return expectValueAs<WasmVirtualMachine.Value.F64>(errorMessage).value
}

inline fun WasmVirtualMachine.Result<List<WasmVirtualMachine.Value>>.expectFirstValue(
    errorMessage: String = "Expectation failure",
): WasmVirtualMachine.Value {
    return expect(errorMessage).firstOrNull() ?: throw IllegalStateException("$errorMessage: result list is empty")
}

inline fun <reified T : WasmVirtualMachine.Value> WasmVirtualMachine.Result<List<WasmVirtualMachine.Value>>.expectFirstValueAs(
    errorMessage: String = "Expectation failure",
): T {
    return expectFirstValue(errorMessage) as? T
        ?: throw IllegalStateException("$errorMessage: value is not of expected type")
}

inline fun WasmVirtualMachine.Result<List<WasmVirtualMachine.Value>>.expectFirstInt(
    errorMessage: String = "Expectation failure",
): Int {
    return expectFirstValueAs<WasmVirtualMachine.Value.I32>(errorMessage).value
}

inline fun WasmVirtualMachine.Result<List<WasmVirtualMachine.Value>>.expectFirstLong(
    errorMessage: String = "Expectation failure",
): Long {
    return expectFirstValueAs<WasmVirtualMachine.Value.I64>(errorMessage).value
}

inline fun WasmVirtualMachine.Result<List<WasmVirtualMachine.Value>>.expectFirstFloat(
    errorMessage: String = "Expectation failure",
): Float {
    return expectFirstValueAs<WasmVirtualMachine.Value.F32>(errorMessage).value
}

inline fun WasmVirtualMachine.Result<List<WasmVirtualMachine.Value>>.expectFirstDouble(
    errorMessage: String = "Expectation failure",
): Double {
    return expectFirstValueAs<WasmVirtualMachine.Value.F64>(errorMessage).value
}

inline fun WasmVirtualMachine.importFactory(
    store: Store,
    imports: List<CodegenImport>,
): List<Import> {
    return imports.map { import ->
        val externalAddress = when (import) {
            is FunctionImport -> {
                allocateFunction(store, import.type, import.function).expect("Failed to allocate function ${import.moduleName}:${import.entityName}")
            }
        }

        Import(
            moduleName = import.moduleName,
            entityName = import.entityName,
            address = externalAddress,
        )
    }
}
