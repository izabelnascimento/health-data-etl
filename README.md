## health-data-etl

The **health-data-etl** application is responsible for the ingestion, transformation, storage, and exposure of public health data through a structured ETL pipeline. It integrates heterogeneous data from Brazilian governmental systems and makes them available via REST APIs for analytical consumption.

This service is the reference implementation of the **Data Sources → Ingestion → Processing → Storage → Interaction** components of the [HERO (Health Efficiency Resource Oriented) architecture](#-related-publication), and works in conjunction with the analytical client:

➡️ Analytical client: https://github.com/izabelnascimento/health-data-client

### 🧱 Architecture / Package Layout
The application follows a modular architecture that separates responsibilities across three layers, as described in the HERO paper:

- **`common/`** — shared components used across the application: JPA entities (`model`), DTOs, mappers, Spring Data repositories (`loader`), utility/validation classes, and configuration (CORS, Swagger).
- **`etl/`** — the ETL pipeline itself, organized per data source: `source` (access to SIOPS/SISAB/e-Gestor), `extractor` (raw data extraction), `transformer` (validation, standardization, and enrichment), and `service`/`controller` (pipeline orchestration and trigger endpoints).
- **`api/`** — the read-side REST API: `service` and `controller` classes that expose the refined, integrated data (budget, coverage, efficiency, DEA indicators, health care visits) for analytical consumption.

Data validation follows a consistent protocol across all three sources: mandatory fields (municipality identifier, reference year, bimester) are checked for non-null values and type consistency, municipality identifiers are standardized against the official IBGE municipality code table, missing numeric values are flagged and excluded (never imputed), and duplicate records are removed based on a composite key of `(municipality, year, bimester)`.

### 🔗 Data Sources
- **SIOPS** (Public Health Budget Information System) — financial data, extracted through a documented REST API: http://siops.datasus.gov.br/filtro_rel_ges_dt_municipal.php
- **SISAB** (Primary Health Care Information System) — service-delivery data, extracted via web scraping and CSV downloads (no public API is available): https://sisab.saude.gov.br/paginas/acessoRestrito/relatorio/federal/saude/RelSauProducao.xhtml
- **e-Gestor APS** — structural/coverage data, extracted through reverse-engineered API endpoints: https://relatorioaps.saude.gov.br/cobertura/aps

### ⚙️ Required Dependencies
- **Java 24 (JDK)** — configured via the Gradle toolchain in `build.gradle`.
- **Gradle** — the wrapper (`./gradlew` / `gradlew.bat`) is included, so no local Gradle installation is required.
- **PostgreSQL** — used as the relational, refined storage layer (Data Lakehouse's Refined Zone).
- **R**, with `Rscript` available on the `PATH`, plus the `jsonlite` and `Benchmarking` packages — used to compute DEA efficiency scores (`src/main/resources/run_DEA.R`):
  ```r
  install.packages(c("jsonlite", "Benchmarking"))
  ```
- Application-level Java dependencies (Spring Boot 3.5, Spring Data JPA, Spring Web/WebFlux, springdoc-openapi/Swagger UI, Jsoup, Lombok, PostgreSQL JDBC driver) are declared in `build.gradle` and resolved automatically by Gradle — no manual installation needed.

### 🗄️ Database Configuration
The application uses PostgreSQL as its relational store, configured in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hero
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

Before the first run:

1. Install and start PostgreSQL locally (or point the URL below to a remote instance).
2. Create an empty database named `hero`:
   ```sql
   CREATE DATABASE hero;
   ```
3. No manual migration is required: `spring.jpa.hibernate.ddl-auto=update` creates and updates the schema automatically on startup, based on the JPA entities under `common/model` (e.g. `City`, `Budget`, `Coverage`, `HealthCareVisit`, `DeaIndicator`).

### 🔐 Environment Variables
None of the values above are hardcoded requirements — every property in `application.properties` can be overridden at runtime through Spring Boot's standard environment-variable binding, without touching the source code. This is the recommended way to configure the application outside of local development (e.g. staging, CI, containers):

| Variable | Overrides | Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `spring.datasource.url` | `jdbc:postgresql://localhost:5432/hero` |
| `SPRING_DATASOURCE_USERNAME` | `spring.datasource.username` | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | `spring.datasource.password` | `postgres` |
| `SERVER_PORT` | `server.port` | `8080` |

Example:
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/hero
export SPRING_DATASOURCE_USERNAME=hero_user
export SPRING_DATASOURCE_PASSWORD=change_me
./gradlew bootRun
```

### ▶️ Running the ETL Pipeline
1. Configure the database (see [Database Configuration](#%EF%B8%8F-database-configuration)) and, optionally, the environment variables above.
2. Start the application:
   ```bash
   ./gradlew bootRun
   ```
3. With the app running, trigger the ETL for each source through its controller under `etl/etl/controller` (`CityETLController`, `BudgetETLController` for SIOPS, `HealthCareVisitETLController` for SISAB, `CoverageETLController` for e-Gestor). Endpoints accept the reporting period (year / bimester) as parameters, so the same pipeline logic can be re-executed for any period covered by the source systems, without code changes.
   > _Exact routes and request parameters are documented live via Swagger (next step) — update this list with the concrete paths as they stabilize._
4. Explore and trigger endpoints, and inspect the exposed schema, via Swagger UI: http://localhost:8080/swagger-ui/index.html
5. Once budget, coverage, and health-care-visit data are loaded for a given period, call the DEA indicator endpoint (`api/service/DeaIndicatorService`) to compute efficiency scores. This step shells out to `run_DEA.R` via `Rscript`, so make sure R and the `Benchmarking`/`jsonlite` packages are installed (see [Required Dependencies](#%EF%B8%8F-required-dependencies)).
6. Consume the refined, integrated data from the `health-data-client` analytical application, or directly through the exposed REST endpoints.

### 🧩 Extending the Pipeline (Example Configuration)
To add a new data source, transformation rule, or derived indicator, follow the same structure used by the existing sources (SIOPS/SISAB/e-Gestor):

- `etl/source/` — describe how to reach the new source (API client, scraper, etc.), following the pattern in `Sisab.java` / `Siops.java` / `EGestor.java`.
- `etl/extractor/` — extract the raw data from the source.
- `etl/transformer/` — apply the shared validation/standardization protocol (mandatory-field checks, IBGE municipality-code normalization, missing-value flagging, duplicate removal by `(municipality, year, bimester)`).
- `common/model/`, `common/dto/`, `common/mapper/` — add the corresponding JPA entity, DTO, and mapper.
- `common/loader/` — add the Spring Data repository for the new entity.
- `etl/service/` + `etl/controller/` — orchestrate the pipeline and expose a trigger endpoint for the new source.
- `api/service/` + `api/controller/` — expose the refined data for analytical consumption (e.g. as a new DEA input/output or a standalone indicator).

> 📝 _TODO: add a minimal, concrete example (e.g. a sample `XyzExtractor`/`XyzTransformation` pair and a sample request/response) illustrating how to plug in a new data source end-to-end._

### 📄 Related Publication
This application supports the experiments described in the HERO architecture paper:

- SBSI 2025 (SmartAPSUS project context): https://sol.sbc.org.br/index.php/sbsi/article/view/34367
- SBCAS 2025 (DEA proof of concept): https://sol.sbc.org.br/index.php/sbcas/article/view/35541
- SBSI 2026 (Primary Healthcare Efficiency Indicators): not available yet

🔗 Swagger: http://localhost:8080/swagger-ui/index.html

🔗 Repository: https://github.com/izabelnascimento/health-data-etl
