package io.github.charlietap.chasm.embedding.shapes

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.RuntimeConfig
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentPreparationCacheTest {

    @Test
    fun `does not cache failed preparation`() {
        val config = RuntimeConfig()
        val cache = ComponentPreparationCache<Int>()
        var preparationCalls = 0
        val prepare: () -> Result<Int, String> = {
            preparationCalls++
            if (preparationCalls == 1) Err("failed") else Ok(117)
        }

        val first = cachedPreparation(config, cache, prepare)
        val second = cachedPreparation(config, cache, prepare)
        val actual = Triple(first, second, preparationCalls)

        val expected = Triple(Err("failed"), Ok(117), 2)
        assertEquals(expected, actual)
    }

    @Test
    fun `reuses preparation when only runtime configuration changes`() {
        val firstConfig = RuntimeConfig(gcThreshold = GCThreshold.MB(1))
        val secondConfig = firstConfig.copy(gcThreshold = GCThreshold.MB(2))
        val cache = ComponentPreparationCache<Int>()
        var preparationCalls = 0
        val prepare: () -> Result<Int, String> = {
            preparationCalls++
            Ok(117)
        }

        val first = cachedPreparation(firstConfig, cache, prepare)
        val second = cachedPreparation(secondConfig, cache, prepare)
        val actual = Triple(first, second, preparationCalls)

        val expected = Triple(Ok(117), Ok(117), 1)
        assertEquals(expected, actual)
    }

    @Test
    fun `retains preparation when compiler configuration changes`() {
        val firstConfig = RuntimeConfig(bytecodeFusion = true)
        val secondConfig = firstConfig.copy(bytecodeFusion = false)
        val cache = ComponentPreparationCache<Int>()
        var preparationCalls = 0
        val prepare: () -> Result<Int, String> = {
            preparationCalls++
            Ok(preparationCalls)
        }

        val first = cachedPreparation(firstConfig, cache, prepare)
        val second = cachedPreparation(secondConfig, cache, prepare)
        val third = cachedPreparation(firstConfig, cache, prepare)
        val actual = listOf(first, second, third) to preparationCalls

        val expected = listOf(Ok(1), Ok(2), Ok(1)) to 2
        assertEquals(expected, actual)
    }
}
