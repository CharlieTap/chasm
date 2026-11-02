package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.ExperimentalChasmApi

@ExperimentalChasmApi
fun compiledModule(
    moduleClass: Class<*>,
): CompiledModule {
    val instance = moduleClass.getField(INSTANCE_FIELD).get(null)
    return instance as? CompiledModule
        ?: throw IllegalArgumentException("${moduleClass.name} is not a compiled module class")
}

@ExperimentalChasmApi
fun compiledModule(
    className: String,
    loader: ClassLoader,
): CompiledModule = compiledModule(Class.forName(className, true, loader))

private const val INSTANCE_FIELD = "INSTANCE"
