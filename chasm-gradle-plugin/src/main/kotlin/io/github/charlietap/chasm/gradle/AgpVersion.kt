package io.github.charlietap.chasm.gradle

data class AgpVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
) {
    override fun toString(): String = "$major.$minor.$patch"

    companion object {
        fun detect(): AgpVersion? {
            val version = runCatching {
                val versionClass = Class.forName("com.android.Version")
                val field = versionClass.getField("ANDROID_GRADLE_PLUGIN_VERSION")
                field.get(null) as String
            }.getOrNull() ?: return null

            return parse(version)
        }

        private fun parse(version: String): AgpVersion? {
            val sanitized = version.substringBefore('-').substringBefore('+').substringBefore('_')
            val parts = sanitized.split('.')
            val major = parts.getOrNull(0)?.toIntOrNull() ?: return null
            val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0
            return AgpVersion(major, minor, patch)
        }
    }
}
