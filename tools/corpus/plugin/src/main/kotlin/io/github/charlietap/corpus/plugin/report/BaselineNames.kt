package io.github.charlietap.corpus.plugin.report

import io.github.charlietap.corpus.lib.report.CorpusEnvironment
import io.github.charlietap.corpus.plugin.ANDROID_DEVICE

class BaselineNames {

    fun name(environment: CorpusEnvironment): String {
        return if (environment.name == ANDROID_DEVICE) {
            val model = checkNotNull(environment.machine.model) { "a device report must name its model" }
            "android-${model.replace(UNSAFE_FILE_NAME_CHARACTERS, "-")}.json"
        } else {
            "${environment.name}.json"
        }
    }

    private companion object {
        val UNSAFE_FILE_NAME_CHARACTERS = Regex("[^A-Za-z0-9._-]+")
    }
}
