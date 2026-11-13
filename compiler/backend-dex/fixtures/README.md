# DEX backend fixtures

`routes.py` writes `routes.wat`: functions whose size estimates send them down each lowering route.
With the argument `eh` it also raises an exception, so every slot starts initialised and whole
attempts hand their slot-kind flow to the split. Generate the binaries from the repository root with:

```sh
python3 compiler/backend-dex/fixtures/routes.py > /tmp/routes.wat
wasm-tools parse /tmp/routes.wat -o compiler/backend-dex/src/jvmTest/resources/fixtures/routes.wasm
python3 compiler/backend-dex/fixtures/routes.py eh > /tmp/routes_eh.wat
wasm-tools parse /tmp/routes_eh.wat -o compiler/backend-dex/src/jvmTest/resources/fixtures/routes_eh.wasm
```
