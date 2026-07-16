package io.github.charlietap.chasm.runtime.component.index

import kotlin.jvm.JvmInline

@JvmInline
value class RuntimeComponentInstanceIndex(val index: Int)

@JvmInline
value class RuntimeCoreInstanceIndex(val index: Int)

@JvmInline
value class RuntimeCoreFunctionIndex(val index: Int)

@JvmInline
value class RuntimeMemoryIndex(val index: Int)

@JvmInline
value class RuntimeReallocIndex(val index: Int)

@JvmInline
value class RuntimePostReturnIndex(val index: Int)

@JvmInline
value class RuntimeResourceTypeIndex(val index: Int)

@JvmInline
value class RuntimeHostFunctionIndex(val index: Int)

@JvmInline
value class PreparedComponentFunctionIndex(val index: Int)

@JvmInline
value class LinearMemoryLayoutIndex(val index: Int)

@JvmInline
value class CanonicalCallPlanIndex(val index: Int)
