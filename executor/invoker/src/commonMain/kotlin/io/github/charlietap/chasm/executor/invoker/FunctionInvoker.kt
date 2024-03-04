package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

typealias FunctionInvoker = (Store, Address.Function, List<ExecutionValue>) -> Result<List<ExecutionValue>, InvocationError>
