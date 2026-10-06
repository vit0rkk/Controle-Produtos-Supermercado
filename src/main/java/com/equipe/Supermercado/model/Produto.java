package com.equipe.Supermercado.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity // Diz ao Spring que isto vai ser uma tabela no banco de dados
@Table(name = "produtos")
public class Produto {

    @Id // Diz que este é o ID (chave primária)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O banco vai gerar os números 1, 2, 3 automaticamente
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigoBarras;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    // Construtor vazio (obrigatório para o banco de dados funcionar)
    public Produto() {
    }

    // Getters e Setters (para o resto da equipa conseguir ler e alterar os dados)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public Integer getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(Integer quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }
}