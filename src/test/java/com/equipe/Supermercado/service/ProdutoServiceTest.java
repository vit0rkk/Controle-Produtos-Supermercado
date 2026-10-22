package com.equipe.Supermercado.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.equipe.Supermercado.model.Produto;
import com.equipe.Supermercado.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoService service;

    private Produto produtoValido() {
        Produto p = new Produto();
        p.setCodigoBarras("789000111");
        p.setNome("Arroz 5kg");
        p.setPreco(new BigDecimal("25.90"));
        p.setQuantidadeEstoque(10);
        return p;
    }

    private void assertErro(String mensagemEsperada, Produto produto) {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrarProduto(produto));
        assertEquals(mensagemEsperada, erro.getMessage());
        verify(repository, never()).save(any(Produto.class));
    }

    @Test
    void cadastraProdutoValido() {
        Produto p = produtoValido();
        when(repository.save(p)).thenReturn(p);

        Produto salvo = service.cadastrarProduto(p);

        assertSame(p, salvo);
        verify(repository).save(p);
    }

    @Test
    void aceitaEstoqueInicialZero() {
        Produto p = produtoValido();
        p.setQuantidadeEstoque(0);
        when(repository.save(p)).thenReturn(p);

        assertSame(p, service.cadastrarProduto(p));
    }

    @Test
    void rejeitaNomeNulo() {
        Produto p = produtoValido();
        p.setNome(null);
        assertErro("Nome obrigatório!", p);
    }

    @Test
    void rejeitaNomeEmBranco() {
        Produto p = produtoValido();
        p.setNome("   ");
        assertErro("Nome obrigatório!", p);
    }

    @Test
    void rejeitaCodigoNulo() {
        Produto p = produtoValido();
        p.setCodigoBarras(null);
        assertErro("Código obrigatório!", p);
    }

    @Test
    void rejeitaCodigoEmBranco() {
        Produto p = produtoValido();
        p.setCodigoBarras("");
        assertErro("Código obrigatório!", p);
    }

    @Test
    void rejeitaCodigoDuplicado() {
        Produto p = produtoValido();
        when(repository.findByCodigoBarras("789000111")).thenReturn(Optional.of(new Produto()));
        assertErro("Produto já cadastrado!", p);
    }

    @Test
    void rejeitaPrecoNulo() {
        Produto p = produtoValido();
        p.setPreco(null);
        assertErro("Preço inválido!", p);
    }

    @Test
    void rejeitaPrecoZero() {
        Produto p = produtoValido();
        p.setPreco(BigDecimal.ZERO);
        assertErro("Preço inválido!", p);
    }

    @Test
    void rejeitaPrecoNegativo() {
        Produto p = produtoValido();
        p.setPreco(new BigDecimal("-1.00"));
        assertErro("Preço inválido!", p);
    }

    @Test
    void rejeitaEstoqueNulo() {
        Produto p = produtoValido();
        p.setQuantidadeEstoque(null);
        assertErro("Estoque inválido!", p);
    }

    @Test
    void rejeitaEstoqueNegativo() {
        Produto p = produtoValido();
        p.setQuantidadeEstoque(-5);
        assertErro("Estoque inválido!", p);
    }
}
