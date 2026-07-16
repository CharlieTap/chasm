package io.github.charlietap.chasm.embedding.shapes

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.RuntimeConfig
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleCompilationCacheTest {

    @Test
    fun `does not cache failed compilation`() {
        val config = RuntimeConfig()
        var cache: ModuleCompilationCache<Int>? = null
        var compilationCalls = 0
        val compiler: () -> Result<Int, String> = {
            compilationCalls++
            if (compilationCalls == 1) Err("failed") else Ok(117)
        }
        val cacheCompiled: (ModuleCompilationCache<Int>) -> Unit = { compiled ->
            cache = compiled
        }

        val first = cachedCompilation(config, cache, compiler, cacheCompiled)
        val second = cachedCompilation(config, cache, compiler, cacheCompiled)
        val actual = Triple(first, second, compilationCalls)

        val expected = Triple(Err("failed"), Ok(117), 2)
        assertEquals(expected, actual)
    }

    @Test
    fun `reuses compilation when only runtime configuration changes`() {
        val firstConfig = RuntimeConfig(gcThreshold = GCThreshold.MB(1))
        val secondConfig = firstConfig.copy(gcThreshold = GCThreshold.MB(2))
        var cache: ModuleCompilationCache<Int>? = null
        var compilationCalls = 0
        val compiler: () -> Result<Int, String> = {
            compilationCalls++
            Ok(117)
        }
        val cacheCompiled: (ModuleCompilationCache<Int>) -> Unit = { compiled ->
            cache = compiled
        }

        val first = cachedCompilation(firstConfig, cache, compiler, cacheCompiled)
        val second = cachedCompilation(secondConfig, cache, compiler, cacheCompiled)
        val actual = Triple(first, second, compilationCalls)

        val expected = Triple(Ok(117), Ok(117), 1)
        assertEquals(expected, actual)
    }

    @Test
    fun `retains compilation when compiler configuration changes`() {
        val firstConfig = RuntimeConfig(bytecodeFusion = true)
        val secondConfig = firstConfig.copy(bytecodeFusion = false)
        var cache: ModuleCompilationCache<Int>? = null
        var compilationCalls = 0
        val compiler: () -> Result<Int, String> = {
            compilationCalls++
            Ok(compilationCalls)
        }
        val cacheCompiled: (ModuleCompilationCache<Int>) -> Unit = { compiled ->
            cache = compiled
        }

        val first = cachedCompilation(firstConfig, cache, compiler, cacheCompiled)
        val second = cachedCompilation(secondConfig, cache, compiler, cacheCompiled)
        val third = cachedCompilation(firstConfig, cache, compiler, cacheCompiled)
        val actual = listOf(first, second, third) to compilationCalls

        val expected = listOf(Ok(1), Ok(2), Ok(1)) to 2
        assertEquals(expected, actual)
    }
}
