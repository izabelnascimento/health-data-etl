## health-data-etl

The **health-data-etl** application is responsible for the ingestion, transformation, storage, and exposure of public health data through a structured ETL pipeline. It integrates heterogeneous data from Brazilian governmental systems and makes them available via REST APIs for analytical consumption.

This service is part of the HERO architecture and works in conjunction with the analytical client:

➡️ Analytical client: https://github.com/izabelnascimento/health-data-client

### 🔗 Data Sources
- SIOPS (Public Health Budget Information System): http://siops.datasus.gov.br/filtro_rel_ges_dt_municipal.php
- SISAB (Primary Health Care Information System): https://sisab.saude.gov.br/paginas/acessoRestrito/relatorio/federal/saude/RelSauProducao.xhtml
- e-Gestor APS: https://relatorioaps.saude.gov.br/cobertura/aps

### 📄 Related Publication
This application supports the experiments described in the HERO architecture paper:

- SBSI 2025 (SmartAPSUS project context): https://sol.sbc.org.br/index.php/sbsi/article/view/34367
- SBCAS 2025 (DEA proof of concept): https://sol.sbc.org.br/index.php/sbcas/article/view/35541
- SBSI 2026 (Primary Healthcare Efficiency Indicators): not available yet

🔗 Swagger: http://localhost:8080/swagger-ui/index.html

🔗 Repository: https://github.com/izabelnascimento/health-data-etl
