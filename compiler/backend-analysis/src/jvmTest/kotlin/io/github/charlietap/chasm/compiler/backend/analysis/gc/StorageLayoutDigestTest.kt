package io.github.charlietap.chasm.compiler.backend.analysis.gc

import io.github.charlietap.chasm.compiled.format.COMPILED_RUNTIME_ABI
import java.security.MessageDigest
import kotlin.test.Test
import kotlin.test.assertEquals

class StorageLayoutDigestTest {

    @Test
    fun `the storage layout matches the digest recorded for this runtime ABI`() {

        val family = STORAGE_FAMILY.classes.joinToString("\n") { storageClass ->
            listOf(storageClass.name, storageClass.superclass?.name.orEmpty(), storageClass.lanes.joinToString(",") { lane -> lane.fieldName() }).joinToString(" ")
        }
        val layout = listOf(family, readGolden("storage_plans.txt"), readGolden("storage_layout_cases.txt")).joinToString("\n")

        assertEquals(RECORDED_ABI, COMPILED_RUNTIME_ABI, "record a new digest when the runtime ABI changes")
        assertEquals(RECORDED_DIGEST, sha256(layout), "the storage layout changed: bump COMPILED_RUNTIME_ABI and record the new digest")
    }

    private fun readGolden(name: String): String {
        return checkNotNull(javaClass.classLoader.getResourceAsStream("goldens/$name")) { "missing golden $name" }
            .use { stream -> stream.readBytes().decodeToString() }
    }

    private fun sha256(text: String): String {
        return MessageDigest.getInstance("SHA-256").digest(text.encodeToByteArray()).toHexString()
    }

    private companion object {
        const val RECORDED_ABI = 5
        const val RECORDED_DIGEST = "a97792c40a40cc8fea9aed3e4135d8ccf7923a6c8e265d9adfe1a4e9fda623c8"
    }
}
