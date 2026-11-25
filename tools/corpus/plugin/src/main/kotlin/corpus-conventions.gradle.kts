import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.plugin.WasmCorpusPlugin
import io.github.charlietap.corpus.plugin.WasmCorpusPluginExtension

apply<WasmCorpusPlugin>()

configure<WasmCorpusPluginExtension> {
    corpusRef = extensions.getByType<VersionCatalogsExtension>().named("libs")
        .findVersion("wasm-corpus-ref").get().requiredVersion
    corpusRunner = "io.github.charlietap.chasm.corpus.PlatformCorpusRunner"
    testPackageName = "io.github.charlietap.chasm.corpus.generated"
    phase = CorpusPhase.INVOCATION
    versions = listOf("1.0", "2.0", "3.0")
    excludedFeatures = listOf("memory64", "simd", "relaxed-simd")
    excludedTags = listOf("stress-test", "benchmark", "duration-extra-long")
    excludedTargets = listOf("esbuild", "jsquash_hqx", "learning_rate_scheduling")
    noisyTargets = listOf("lzma_wasm", "brotli_codec")
}
