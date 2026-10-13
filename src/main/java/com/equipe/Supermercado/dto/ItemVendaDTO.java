package com.equipe.Supermercado.dto;

public class ItemVendaDTO {
    private String codigoBarras;
    private Integer quantidade;

    // Getters e Setters
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
