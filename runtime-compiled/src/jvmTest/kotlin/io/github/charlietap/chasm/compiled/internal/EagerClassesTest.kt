package io.github.charlietap.chasm.compiled.internal

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EagerClassesTest {

    private val loader = EagerClassesTest::class.java.classLoader

    @Test
    fun `classes are initialised once across instances`() = runTest {

        val initializer = CountingInitializer()
        val eager = EagerClasses(loader, CLASSES, initializer)

        eager.initialize()
        eager.initialize()

        assertEquals(1, initializer.calls)
        assertEquals(CLASSES, initializer.classes)
    }

    @Test
    fun `concurrent first instances wait for one initialisation`() = runTest {

        val release = CompletableDeferred<Unit>()
        val initializer = CountingInitializer { release.await() }
        val eager = EagerClasses(loader, CLASSES, initializer)

        val first = async { eager.initialize() }
        val second = async { eager.initialize() }
        runCurrent()

        assertFalse(first.isCompleted)
        assertFalse(second.isCompleted)
        release.complete(Unit)
        first.await()
        second.await()
        assertEquals(1, initializer.calls)
    }

    @Test
    fun `a cancelled initialisation runs again`() = runTest {

        var cancelling = true
        val initializer = CountingInitializer {
            if (cancelling) {
                awaitCancellation()
            }
        }
        val eager = EagerClasses(loader, CLASSES, initializer)

        val first = launch { eager.initialize() }
        runCurrent()
        first.cancelAndJoin()
        cancelling = false
        eager.initialize()

        assertTrue(first.isCancelled)
        assertEquals(2, initializer.calls)
    }

    @Test
    fun `an initialiser that throws is reported again by the next instance`() = runTest {

        val initializer = CountingInitializer { throw NoClassDefFoundError("chasm.compiled.eager.Broken") }
        val eager = EagerClasses(loader, CLASSES, initializer)

        assertFailsWith<NoClassDefFoundError> { eager.initialize() }
        assertFailsWith<NoClassDefFoundError> { eager.initialize() }
        assertEquals(2, initializer.calls)
    }

    @Test
    fun `a module without eager classes initialises nothing`() = runTest {

        val initializer = CountingInitializer()
        val eager = EagerClasses(loader, emptyList(), initializer)

        eager.initialize()

        assertEquals(0, initializer.calls)
    }

    private class CountingInitializer(
        private val work: suspend () -> Unit = {},
    ) : CodeClassInitializer {

        var calls = 0

        var classes: List<String> = emptyList()

        override suspend fun initialize(
            loader: ClassLoader,
            classes: List<String>,
        ) {

            calls++
            this.classes = classes

            work()
        }
    }

    private companion object {
        val CLASSES = listOf("chasm.compiled.eager.Module\$Code0", "chasm.compiled.eager.Module\$Sig0")
    }
}
