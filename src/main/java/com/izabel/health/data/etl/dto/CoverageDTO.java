package com.izabel.health.data.etl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoverageDTO {
    private String nuComp;
    private String coRegiao;
    private String noRegiao;
    private String sgRegiao;
    private String coUfIbge;
    private String noUf;
    private String noUfAcentuado;
    private String sgUf;
    private String coMunicipioIbge;
    private String noMunicipioIbge;
    private String noMunicipioAcentuado;
    private String coClassificacaoTipologia;
    private String nuAnoReferencia;
    private String tpOrigemBasePopulacao;
    private Integer qtPopulacao;
    private Integer qtEsf;
    private Integer qtEap30;
    private Integer qtEap20;
    private Integer qtEsfr;
    private Integer qtCadastroEsfr;
    private Integer qtEcr;
    private Integer qtCadastroEcr;
    private Integer qtEapp20;
    private Integer qtCadastroEapp20;
    private Integer qtEapp30;
    private Integer qtCadastroEapp30;
    private Integer qtCadastroEquipeEsfrEcrEapp;
    private Integer qtCapacidadeEquipe;
    private Double qtCobertura;
}
