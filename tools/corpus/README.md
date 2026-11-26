# Wasm Corpus Plugin

`corpus` is an included Gradle build that runs the
[`CharlieTap/wasm-corpus`](https://github.com/CharlieTap/wasm-corpus) fixtures
on every engine a platform has and writes one report format for all of them.
It has two projects:

- `lib`: the fixture model, the `CorpusRunner` contract, `CorpusSettings`,
  the per-fixture `Measurement` and the report model. It targets every
  platform, so test code on the JVM, Android and native can use it.
- `plugin`: fixture selection, test generation, the environments the corpus
  runs in, the report and the device run.

Chasm applies it once, through `corpus-conventions`, in `test:corpus`. That
module holds the engine seam (`Engine`, `Interpreter`, `ClassFiles`, `Dex`)
and the fixture runner, so the corpus always tests the local source tree.

## Running the corpus

```sh
./gradlew corpus
./gradlew corpusAndroidHost
./gradlew corpusMacosArm64
./gradlew corpusAndroidDevice
```

| Task | Runs on | Report |
|---|---|---|
| `corpus` | the JVM: the interpreter and class files through `chasm-class-compiler` | `test/corpus/build/reports/corpus/jvm.json` |
| `corpusAndroidHost` | the host JVM: the interpreter and ART-target class files | `.../corpus/androidHost.json` |
| `corpusMacosArm64`, `corpusLinuxX64` | the native interpreter, as a release test binary | `.../corpus/<target>.json` |
| `corpusAndroidDevice` | an attached phone: the interpreter and DEX through `chasm-dex-compiler` | `.../corpus/androidDevice.json` |

`./gradlew test`, `testAndroidHostTest`, `macosArm64Test` and `linuxX64Test`
include the JVM, Android host and native runs, because the corpus test is an
ordinary test of `test:corpus`. A native target runs only on its own host:
macOS runs `corpusMacosArm64` and Linux x64 runs `corpusLinuxX64`. The corpus
does not run on mingwX64 (the WASI binding has no Windows build), linuxArm64
(Kotlin gives it no test runs) or iOS.

The native test task executes the release test binary, so the timings come
from optimised code; linking it takes longer than the debug binary. The
fixtures run on the test's main thread with its default stack, because only
the interpreter runs there.

Every run is untracked: it always runs afresh and is never restored from the
build cache, so a report never carries old timings.

## Configuring a run

All settings are compiled into the generated test and packaged with the
fixtures, so the same properties work on every platform.

| Property | Default | Effect |
|---|---|---|
| `-PwasmCorpus.phase` | `invocation` | `decoding`, `validation`, `instantiation`, `invocation` |
| `-PwasmCorpus.targets` | all selected | comma list of fixture names, paths or stems |
| `-PwasmCorpus.engines` | `interpreter,compiled` | which of the platform's engines run |
| `-PwasmCorpus.warmups` | `0` | untimed passes before the measured rounds; needs rounds |
| `-PwasmCorpus.rounds` | `0` | measured passes; `warm` is their median |
| `-PwasmCorpus.pin` | `true` | on a phone, pins the fixture thread to its fastest allowed CPU; `false` opts out |
| `-PwasmCorpus.label` | none | suffix of the report file, for keeping several runs |
| `-PwasmCorpus.device` | the only attached device | serial or model of the phone |
| `-PwasmCorpus.apk` | the built test APK | a prebuilt corpus APK to run instead |
| `-PwasmCorpus.before`, `.after` | the baseline, the latest report | the reports `corpusCompare` compares |

`decoding` and `validation` measure Chasm's front end once per fixture under
the engine name `frontend`. `instantiation` and `invocation` run on each
selected engine. A selection that matches no engine on the platform fails
every fixture.

A round repeats what the phase measures: `invocation` rounds reuse the module
prepared for the cold pass and redo instantiation and execution;
`instantiation` rounds start from the bytes every time.

The fixture selection (versions, excluded features, tags and targets) lives in
`corpus-conventions`.

## Reports

Each fixture's measurement is written as it finishes to
`build/corpus/measurements/<environment>/<engine>/<fixture>.json`, and
`corpusReport<Environment>` aggregates them into the report. The report task
finalizes the test task, so a failing host run still writes its partial
report.

A report records the environment (machine and runtime), the run's
configuration, a summary per engine and every measurement. The engines a run
should produce are known before it starts, so an engine that produced nothing,
or a fixture lost to an exception or a crash, is listed under `missing`.

Each `Timings` holds the total, decoding, validation, module preparation
(bytes to `Module` through the engine), instantiation and execution. `cold` is
the first pass; `warm` is the per-field median of the measured rounds. The
summary prints instantiation as module preparation plus instantiation.

Timings written during `./gradlew test` share the machine with every other
test. They show a fixture that became pathological; comparisons come from the
`corpus*` tasks run on their own.

## Baselines

```sh
./gradlew updateCorpusBaseline
./gradlew updateCorpusBaselineAndroidDevice
```

`updateCorpusBaseline<Environment>` runs the corpus and copies its report to
`baselines/corpus/`: `jvm.json` for the JVM, `android-<model>.json` for a
phone. Every baseline is measured the same way: `baselineWarmups` warm-ups and
`baselineRounds` rounds from the `corpus {}` block (1 and 3 by default), used
automatically when an `updateCorpusBaseline` task is requested by its full
name. It refuses a report measured with other counts, with a missing or failed
fixture, or narrowed to some targets, engines or another phase. No
baseline ever fails a build. Ordinary corpus runs stay a single cold pass.

## Comparing reports

```sh
./gradlew corpusCompare
./gradlew corpusCompareAndroidDevice
```

`corpusCompare<Environment>` prints a comparison and never fails. By default
it compares the environment's latest report (`<environment>.json`, or
`<environment>-<label>.json` with `-PwasmCorpus.label`) against the baseline
for the environment and phone recorded in that report, so comparing a saved
phone report needs no phone attached and no build of the corpus.

`-PwasmCorpus.before` and `-PwasmCorpus.after` take one or more report files,
comma-separated and relative to the repository root. With several files on a
side, each fixture's value is the median across them.

For each engine it prints instantiation (module preparation plus
instantiation, cold), cold execution and, when both sides have rounds, warm
execution: the number of fixtures at or above 1 ms before the change, the
geomean and time-weighted ratios of after over before, how many moved beyond
±3 %, the totals, and the six best and worst fixtures. The `noisyTargets` from
the `corpus {}` block (`lzma_wasm` and `brotli_codec`) are left out of the
ratios and listed separately. It warns when the reports differ in environment,
phone model, phase, engines, targets, warm-ups, rounds, pinning or corpus
revision.

An A/B on a phone builds two APKs and runs them in ABBA order:

```sh
./gradlew :test:corpus:assembleAndroidDeviceTest -PwasmCorpus.engines=compiled -PwasmCorpus.warmups=3 -PwasmCorpus.rounds=5
cp test/corpus/build/outputs/apk/androidTest/*.apk ~/corpus-base.apk
# switch to the change, build again, copy to ~/corpus-work.apk

./gradlew corpusAndroidDevice -PwasmCorpus.apk=$HOME/corpus-base.apk -PwasmCorpus.label=base1
./gradlew corpusAndroidDevice -PwasmCorpus.apk=$HOME/corpus-work.apk -PwasmCorpus.label=work1
./gradlew corpusAndroidDevice -PwasmCorpus.apk=$HOME/corpus-work.apk -PwasmCorpus.label=work2
./gradlew corpusAndroidDevice -PwasmCorpus.apk=$HOME/corpus-base.apk -PwasmCorpus.label=base2

R=test/corpus/build/reports/corpus
./gradlew corpusCompareAndroidDevice \
  -PwasmCorpus.before=$R/androidDevice-base1.json,$R/androidDevice-base2.json \
  -PwasmCorpus.after=$R/androidDevice-work1.json,$R/androidDevice-work2.json
```

Each report is labelled from the `corpus/run.json` packaged in its APK, so
the four runs pass no `engines`, `warmups` or `rounds` and their reports still
say `compiled`, 3 and 5.

## The device run

The device corpus is `test:corpus`'s own device test. The plugin transforms
that APK through the public variant API so it is the shape an app ships:

- `corpusNonDebuggableManifest` rewrites `android:debuggable` to `false` in
  the merged manifest.
- `corpusReleaseR8Classes` runs the R8 that AGP ships in release mode over
  every class in the APK, with the module's `deviceKeepRules`
  (`test/corpus/harness-rules.pro` and `runtime-compiled/consumer-rules.pro`),
  so every device run also exercises the runtime's consumer rules. AGP's D8
  then translates the optimised classes to DEX.

`corpusAndroidDevice` installs the APK, runs the corpus test with
`am instrument -r`, reads each measurement from the instrumentation status
stream and writes the report. The report's configuration and expected
fixtures come from the `corpus/run.json` and `corpus/fixtures.json` packaged
in the APK that ran, so a prebuilt APK passed with `-PwasmCorpus.apk` is
labelled with the settings it was built with:

```sh
./gradlew :test:corpus:assembleAndroidDeviceTest -PwasmCorpus.engines=compiled -PwasmCorpus.warmups=3 -PwasmCorpus.rounds=5
./gradlew corpusAndroidDevice -PwasmCorpus.apk=<path to the apk> -PwasmCorpus.label=base1
```

Before installing, the task wakes the phone and refuses to run unless it is
unlocked, on power and at thermal status 0. For the run it sets
`stay_on_while_plugged_in` to 7, enables fixed-performance mode where the
device supports it and compiles the test APK with
`cmd package compile -m speed -f`; the settings are restored afterwards, also
when the run fails. The fixture thread runs at
`THREAD_PRIORITY_URGENT_DISPLAY`. The report's `environment` names the phone,
its Android and ART versions, and its `conditions` record what took effect:
`debuggable`, `powered`, `fixedPerformanceMode`, `compilerFilter`, thermal
status and battery temperature before and after, and the priority and allowed
CPUs read back on the fixture thread.

Device runs and baselines are pinned by default, so a run never lands on a
little core; `-PwasmCorpus.pin=false` (or `pin = false` in the `corpus {}`
block) opts out. The setting is compiled into the APK and recorded as the
configuration's `pin`. When pinned, the fixture thread reads its `Cpus_allowed_list`, picks the allowed CPU with the
highest `cpuinfo_max_freq` (the lowest index on a tie) and pins itself there
with `taskset -p` on its own thread id; a failed `taskset` fails the fixtures
with its exit code and output.
The allowed CPUs read back then show the single CPU and `conditions` add
`pinnedCpu`. Compilation still runs on unpinned `Dispatchers.Default` threads.
The JVM, Android host and native runs ignore it.

The USB, wireless and `ip:port` transports of one phone count as one device,
and USB is used when present. An emulator is chosen only when no phone is
attached or when it is named. With several phones attached, name one with
`-PwasmCorpus.device` (a serial of any of its transports, its hardware serial
or its `ro.product.model`, as the report names it) or `ANDROID_SERIAL`.

## Fixture tasks

- `syncWasmCorpus` clones or fetches the corpus and checks out the pinned ref.
- `resolveCorpusFixtures` invokes the corpus repository's Node CLI.
- `prepareCorpusResources` packages the selected fixtures, their index and the
  run description as test resources. On native targets the plugin adds them
  to the resources plugin's `<target><Binary>CopyResources` task directly,
  because that task only sees resource directories that exist when the build
  is configured.
- `generateCorpusTests` generates one test method per selected fixture.
- `corpusMatrix` prints fixture counts by version.
- `cleanCorpusTests` removes generated tests, resources, measurements and
  reports; the synced corpus checkout stays.

Generated tests pass a `CorpusResult.Skipped` and fail only on
`CorpusResult.Failure`.
