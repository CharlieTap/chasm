package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

typealias HostFunction = HostFunctionContext.(List<ExecutionValue>) -> List<ExecutionValue>
