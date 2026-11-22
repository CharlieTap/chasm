package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.CompiledCode
import io.github.charlietap.chasm.aot.internal.compiledModule

internal class FakeModuleClass {

    companion object {

        @JvmField
        var INSTANCE: CompiledModule? = null
    }
}

internal class InstanceFieldModuleClass {

    @JvmField
    val INSTANCE: CompiledModule? = null
}

internal class FailingModuleClass {

    companion object {

        @JvmField
        val INSTANCE: CompiledModule? = failingInitializer()
    }
}

private fun failingInitializer(): CompiledModule? {
    throw IllegalStateException("broken initializer")
}

@OptIn(InternalChasmApi::class)
internal fun fakeModuleClass(
    code: CompiledCode,
): Class<*> {
    FakeModuleClass.INSTANCE = compiledModule(code)
    return FakeModuleClass::class.java
}
