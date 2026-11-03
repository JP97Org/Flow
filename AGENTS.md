# AGENTS.md

## Project shape

Single-module Maven project: a Java 21 / Quarkus 3.36 module-based flowchart calculator.

- `src/main/java/org/jojo/flow/Main.java` is only a placeholder; the app is booted by Quarkus, not that `main`.
- `rest/ModelFacadeResource` exposes the HTTP API under `/api/flow`.
- Main packages: `model/api` (interfaces and facades), `model/data`, `model/flowChart`, `model/simulation`, `model/storeLoad` (XML DOM), `rest`.
- `docOriginalFlow/` contains design/report PDFs for the original C++ project; treat them as domain reference, not implementation guidance.

## Commands

- Compile/package without tests: `mvn -B -DskipTests package` (produces `target/flow.jar`)
- Run tests: `mvn -B test`
- Run one test: `mvn -B -Dtest=org.jojo.flow.test.model.data.MathMatrixTest test`
- Run dev server: `./docker-dev.sh` (recommended; mounts the repo, uses JDK 21 Maven image, port 8080)
- Build container image: `./docker-build.sh`

There is no separate lint, formatter, or typecheck configuration; Maven compilation/tests are the verification path.

## Test caveats

- Tests use JUnit Jupiter (`org.junit.jupiter.api`), not JUnit 4; the Quarkus BOM supplies the version. Place new tests under the mirrored `org.jojo.flow.test.*` package.
- Full `mvn test` currently has two pre-existing failures; do not assume your change caused them:
  - `IntegrationTest.simpleTests` hardcodes `/home/jojo/...` paths for external jars and XML output, so it fails outside that machine.
  - `FactoryUtilTest.mapTest` is order-dependent: it passes alone but fails in the full suite because the factory is already initialized by earlier tests.
- `FactoryUtil`, `ModelFacade`, its main flowchart, and its ID counter are global/static state that persists across tests. Focused tests are more reliable than assuming test isolation.

## Architecture conventions

- `model/api` interfaces provide static `getDefaultImplementation()` methods. `model/util/DefaultFactory.initialize()` maps every API interface to its implementation; register new API implementations there or the getter returns `null` and reports a warning.
- `ModelFacade` owns flowchart ID allocation and the main flowchart. REST state lives in the application-scoped `ModelFacadeService`; `POST /api/flow/main` replaces the main flowchart.
- Domain objects follow an interface + default implementation pattern, with exceptions under `flow/exc` and warnings collected via `Warning`.
