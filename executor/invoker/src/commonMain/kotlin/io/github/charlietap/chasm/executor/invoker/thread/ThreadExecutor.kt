package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal typealias ThreadExecutor = (Configuration) -> Result<List<ExecutionValue>, InvocationError>
