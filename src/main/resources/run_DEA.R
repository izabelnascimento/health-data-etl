library(jsonlite)
library(Benchmarking)

dados_bimestre <- fromJSON(file("stdin"))
dadosDEA <- as.data.frame(dados_bimestre)

inputs <- as.matrix(dadosDEA[, c("apsPerCapita", "teamsDensity")])
outputs <- cbind(
  as.matrix(dadosDEA[,"healthCareVisitsPerThousandReais"]),
  as.matrix(dadosDEA[,"productivity"])
)

dea_result <- dea(X = inputs, Y = outputs, RTS = "crs", ORIENTATION = "in")

res <- data.frame(
  cityName = dadosDEA$cityName,
  bimonthly = dadosDEA$bimonthly,
  cityId = dadosDEA$cityId,
  efficiency = dea_result$eff
)

cat(toJSON(res, pretty = FALSE))
