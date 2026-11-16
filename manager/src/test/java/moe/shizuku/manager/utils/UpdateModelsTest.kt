package moe.shizuku.manager.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateModelsTest {

    @Test
    fun parsesStableVersion() {
        assertEquals(Version(13, 9, 1, 0), Version.parse("13.9.1-dwicao"))
        assertEquals(Version(13, 9, 1, 0), Version.parse("v13.9.1-dwicao"))
    }

    @Test
    fun parsesBetaVersion() {
        assertEquals(Version(13, 9, 1, 1370), Version.parse("13.9.1-r1370-beta-dwicao"))
    }

    @Test
    fun rejectsInvalidVersion() {
        assertNull(Version.parse("not a version"))
        assertNull(Version.parse("13.9"))
    }

    @Test
    fun ordersBetaAfterMatchingStable() {
        assertTrue(Version.parse("13.9.1-r1-beta-dwicao")!! > Version.parse("13.9.1-dwicao")!!)
        assertTrue(Version.parse("13.10.0-dwicao")!! > Version.parse("13.9.1-r9999-beta-dwicao")!!)
    }

    @Test
    fun parsesVersionJson() {
        val parsed = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }.decodeFromString(
            VersionJson.serializer(),
            """
            {
              "stable": {
                "versionName": "13.9.1-dwicao",
                "apkUrl": "https://example.test/app.apk",
                "sha256": "abc123"
              },
              "beta": null
            }
            """.trimIndent()
        )

        assertEquals("13.9.1-dwicao", parsed.stable?.versionName)
        assertEquals("abc123", parsed.stable?.sha256)
        assertEquals(null, parsed.beta)
    }
}
