package io.github.charlietap.chasm.fixture.runtime.component.index

import io.github.charlietap.chasm.runtime.component.index.CanonicalCallPlanIndex
import io.github.charlietap.chasm.runtime.component.index.LinearMemoryLayoutIndex
import io.github.charlietap.chasm.runtime.component.index.PreparedComponentFunctionIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeCoreFunctionIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeCoreInstanceIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeHostFunctionIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeMemoryIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimePostReturnIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeReallocIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex

fun runtimeComponentInstanceIndex(index: Int = 0) = RuntimeComponentInstanceIndex(index)

fun runtimeCoreInstanceIndex(index: Int = 0) = RuntimeCoreInstanceIndex(index)

fun runtimeCoreFunctionIndex(index: Int = 0) = RuntimeCoreFunctionIndex(index)

fun runtimeMemoryIndex(index: Int = 0) = RuntimeMemoryIndex(index)

fun runtimeReallocIndex(index: Int = 0) = RuntimeReallocIndex(index)

fun runtimePostReturnIndex(index: Int = 0) = RuntimePostReturnIndex(index)

fun runtimeResourceTypeIndex(index: Int = 0) = RuntimeResourceTypeIndex(index)

fun runtimeHostFunctionIndex(index: Int = 0) = RuntimeHostFunctionIndex(index)

fun preparedComponentFunctionIndex(index: Int = 0) = PreparedComponentFunctionIndex(index)

fun linearMemoryLayoutIndex(index: Int = 0) = LinearMemoryLayoutIndex(index)

fun canonicalCallPlanIndex(index: Int = 0) = CanonicalCallPlanIndex(index)
