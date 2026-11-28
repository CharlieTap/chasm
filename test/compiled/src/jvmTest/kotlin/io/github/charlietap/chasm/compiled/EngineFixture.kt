package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.embedding.engine.Engine
import java.lang.reflect.Proxy

fun fakeEngine(): Engine {
    return Proxy.newProxyInstance(Engine::class.java.classLoader, arrayOf(Engine::class.java)) { _, method, _ ->
        throw UnsupportedOperationException("${method.name} is not used by these tests")
    } as Engine
}
