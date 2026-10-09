
package com.equipe.Supermercado.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.equipe.Supermercado.model.Produto;
import com.equipe.Supermercado.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public Produto cadastrarProduto(Produto produto) {

        if (produto.getNome() == null ||
            produto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome obrigatório!");
        }

        if (produto.getCodigoBarras() == null ||
            produto.getCodigoBarras().isBlank()) {
            throw new IllegalArgumentException("Código obrigatório!");
        }

        if (repository.findByCodigoBarras(
                produto.getCodigoBarras()).isPresent()) {
            throw new IllegalArgumentException("Produto já cadastrado!");
        }

        if (produto.getPreco() == null ||
            produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço inválido!");
        }

        if (produto.getQuantidadeEstoque() == null ||
            produto.getQuantidadeEstoque() < 0) {
            throw new IllegalArgumentException("Estoque inválido!");
        }

        return repository.save(produto);
    }
}
