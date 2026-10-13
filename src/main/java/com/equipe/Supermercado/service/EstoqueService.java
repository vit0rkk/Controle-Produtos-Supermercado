package com.equipe.Supermercado.service;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.equipe.Supermercado.model.Produto;
import com.equipe.Supermercado.model.Movimentacao;
import com.equipe.Supermercado.repository.ProdutoRepository;
import com.equipe.Supermercado.repository.MovimentacaoRepository;

@Service
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public EstoqueService(ProdutoRepository produtoRepository, MovimentacaoRepository movimentacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

  // regra de negócio 1
    public Integer obterQuantidadePorCodigo(String codigoBarras) {
        Produto produto = produtoRepository.findByCodigoBarras(codigoBarras)
                .orElseThrow(() -> new IllegalArgumentException("Produto com código " + codigoBarras + " não encontrado!"));
        return produto.getQuantidadeEstoque();
    }

   // regra de negócio 2
    @Transactional
    public Produto processarSaidaFrenteCaixa(String codigoBarras, Integer quantidadeSolicitada) {
        if (quantidadeSolicitada <= 0) {
            throw new IllegalArgumentException("A quantidade da venda deve ser maior que zero!");
        }

        // busca o produto pelo código de barras
        Produto produto = produtoRepository.findByCodigoBarras(codigoBarras)
                .orElseThrow(() -> new IllegalArgumentException("Produto não cadastrado no sistema!"));

       // impede venda se o estoque estiver zerado
        if (produto.getQuantidadeEstoque() <= 0) {
            throw new IllegalArgumentException("Venda bloqueada: O produto '" + produto.getNome() + "' está totalmente sem estoque!");
        }

        // impede venda se a quantidade solicitada for maior que a quantidade em estoque
        if (produto.getQuantidadeEstoque() < quantidadeSolicitada) {
            throw new IllegalArgumentException("Venda bloqueada: Estoque insuficiente para '" + produto.getNome() + "'. " +
                    "Disponível: " + produto.getQuantidadeEstoque() + " | Solicitado: " + quantidadeSolicitada);
        }

        // deduz a quantidade do estoque do produto
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidadeSolicitada);
        Produto produtoAtualizado = produtoRepository.save(produto);

        // registra a movimentação de saída no estoque
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setProduto(produtoAtualizado);
        movimentacao.setTipo("SAIDA");
        movimentacao.setQuantidade(quantidadeSolicitada);
        movimentacaoRepository.save(movimentacao);

        return produtoAtualizado;
    }
}
