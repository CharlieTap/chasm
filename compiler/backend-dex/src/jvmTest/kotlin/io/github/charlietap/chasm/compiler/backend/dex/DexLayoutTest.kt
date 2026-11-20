package io.github.charlietap.chasm.compiler.backend.dex

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DexLayoutTest {

    @Test
    fun `a module within one worker's share of work is one file`() {
        assertEquals(listOf(0..2), contiguousFiles(longArrayOf(10_000, 10_000, 12_000)))
    }

    @Test
    fun `no batches means no files`() {
        assertEquals(emptyList(), contiguousFiles(LongArray(0)))
    }

    @Test
    fun `files cover every batch once in order without empty files`() {
        val random = Random(7)
        repeat(200) {
            val work = LongArray(random.nextInt(1, 400)) { random.nextLong(64, 6_000) }
            val files = contiguousFiles(work)

            assertEquals(0, files.first().first)
            assertEquals(work.lastIndex, files.last().last)
            files.zipWithNext().forEach { (previous, next) -> assertEquals(previous.last + 1, next.first) }
            assertTrue(files.none(IntRange::isEmpty))
            assertTrue(files.size <= 64)
        }
    }

    @Test
    fun `large modules split into files of similar work`() {
        val files = contiguousFiles(LongArray(100) { 1_000 })

        assertEquals(25, files.size)
        assertTrue(files.all { file -> file.count() == 4 })
    }
}
