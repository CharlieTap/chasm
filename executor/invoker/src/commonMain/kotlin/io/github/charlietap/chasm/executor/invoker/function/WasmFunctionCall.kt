package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias WasmFunctionCall = (Store, Stack, FunctionInstance.WasmFunction, Boolean) -> Result<Unit, InvocationError>
