library(jsonlite)
library(Benchmarking)

dados_bimestre <- fromJSON(file("stdin"))
dadosDEA <- as.data.frame(dados_bimestre)

inputs <- as.matrix(dadosDEA[, c("recursoAPSperCapita", "densidadeEquipes")])
outputs <- cbind(
  as.matrix(dadosDEA[,"atendimentos1000"]),
  as.matrix(dadosDEA[,"produtividade"])
)

dea_result <- dea(X = inputs, Y = outputs, RTS = "crs", ORIENTATION = "in")

res <- data.frame(
  cityId = dadosDEA$cityId,
  efficiency = dea_result$eff
)

cat(toJSON(res, pretty = FALSE))
