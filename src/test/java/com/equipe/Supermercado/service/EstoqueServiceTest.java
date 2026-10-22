package com.equipe.Supermercado.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.equipe.Supermercado.model.Movimentacao;
import com.equipe.Supermercado.model.Produto;
import com.equipe.Supermercado.repository.MovimentacaoRepository;
import com.equipe.Supermercado.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    private static final String CODIGO = "789000111";

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @InjectMocks
    private EstoqueService service;

    private Produto produtoComEstoque(int estoque) {
        Produto p = new Produto();
        p.setCodigoBarras(CODIGO);
        p.setNome("Arroz 5kg");
        p.setPreco(new BigDecimal("25.90"));
        p.setQuantidadeEstoque(estoque);
        return p;
    }

    // ---------- obterQuantidadePorCodigo ----------

    @Test
    void retornaQuantidadeDoProdutoExistente() {
        when(produtoRepository.findByCodigoBarras(CODIGO))
                .thenReturn(Optional.of(produtoComEstoque(7)));

        assertEquals(7, service.obterQuantidadePorCodigo(CODIGO));
    }

    @Test
    void consultaDeProdutoInexistenteLancaErro() {
        when(produtoRepository.findByCodigoBarras("000")).thenReturn(Optional.empty());

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> service.obterQuantidadePorCodigo("000"));
        assertTrue(erro.getMessage().contains("não encontrado"));
    }

    // ---------- processarSaidaFrenteCaixa ----------

    @Test
    void saidaValidaDescontaEstoqueERegistraMovimentacao() {
        Produto p = produtoComEstoque(10);
        when(produtoRepository.findByCodigoBarras(CODIGO)).thenReturn(Optional.of(p));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(i -> i.getArgument(0));

        Produto resultado = service.processarSaidaFrenteCaixa(CODIGO, 3);

        assertEquals(7, resultado.getQuantidadeEstoque());

        ArgumentCaptor<Movimentacao> captor = ArgumentCaptor.forClass(Movimentacao.class);
        verify(movimentacaoRepository).save(captor.capture());
        Movimentacao mov = captor.getValue();
        assertEquals("SAIDA", mov.getTipo());
        assertEquals(3, mov.getQuantidade());
        assertSame(p, mov.getProduto());
    }

    @Test
    void permiteVenderTodoOEstoqueDisponivel() {
        Produto p = produtoComEstoque(5);
        when(produtoRepository.findByCodigoBarras(CODIGO)).thenReturn(Optional.of(p));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(i -> i.getArgument(0));

        Produto resultado = service.processarSaidaFrenteCaixa(CODIGO, 5);

        assertEquals(0, resultado.getQuantidadeEstoque());
    }

    @Test
    void rejeitaQuantidadeZero() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> service.processarSaidaFrenteCaixa(CODIGO, 0));
        assertEquals("A quantidade da venda deve ser maior que zero!", erro.getMessage());
        verify(produtoRepository, never()).findByCodigoBarras(any());
        verify(movimentacaoRepository, never()).save(any(Movimentacao.class));
    }

    @Test
    void rejeitaQuantidadeNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> service.processarSaidaFrenteCaixa(CODIGO, -2));
        verify(movimentacaoRepository, never()).save(any(Movimentacao.class));
    }

    @Test
    void rejeitaProdutoNaoCadastrado() {
        when(produtoRepository.findByCodigoBarras("000")).thenReturn(Optional.empty());

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> service.processarSaidaFrenteCaixa("000", 1));
        assertEquals("Produto não cadastrado no sistema!", erro.getMessage());
        verify(movimentacaoRepository, never()).save(any(Movimentacao.class));
    }

    @Test
    void bloqueiaVendaQuandoEstoqueZerado() {
        when(produtoRepository.findByCodigoBarras(CODIGO))
                .thenReturn(Optional.of(produtoComEstoque(0)));

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> service.processarSaidaFrenteCaixa(CODIGO, 1));
        assertTrue(erro.getMessage().contains("sem estoque"));
        verify(produtoRepository, never()).save(any(Produto.class));
        verify(movimentacaoRepository, never()).save(any(Movimentacao.class));
    }

    @Test
    void bloqueiaVendaQuandoEstoqueInsuficiente() {
        Produto p = produtoComEstoque(2);
        when(produtoRepository.findByCodigoBarras(CODIGO)).thenReturn(Optional.of(p));

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> service.processarSaidaFrenteCaixa(CODIGO, 5));
        assertTrue(erro.getMessage().contains("Estoque insuficiente"));
        assertEquals(2, p.getQuantidadeEstoque());
        verify(produtoRepository, never()).save(any(Produto.class));
        verify(movimentacaoRepository, never()).save(any(Movimentacao.class));
    }
}
