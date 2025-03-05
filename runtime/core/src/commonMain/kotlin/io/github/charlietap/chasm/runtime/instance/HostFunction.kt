package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.runtime.value.ExecutionValue

typealias HostFunction = HostFunctionContext.(List<ExecutionValue>) -> List<ExecutionValue>
