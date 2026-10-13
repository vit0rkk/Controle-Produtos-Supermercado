package com.equipe.Supermercado.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.equipe.Supermercado.model.Produto;
import com.equipe.Supermercado.dto.ItemVendaDTO;
import com.equipe.Supermercado.service.EstoqueService;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    //Endpoint para a Frente de Caixa consultar o estoque de um item antes de bipar
      // GET http://localhost:8080/estoque/quantidade/{codigoBarras}
    
    @GetMapping("/quantidade/{codigoBarras}")
    public ResponseEntity<?> obterQuantidade(@PathVariable String codigoBarras) {
        try {
            Integer quantidade = estoqueService.obterQuantidadePorCodigo(codigoBarras);
            return ResponseEntity.ok().body(quantidade);
        } catch (IllegalArgumentException erro) {
            return ResponseEntity.badRequest().body(erro.getMessage());
        }
    }

   
     // Endpoint para registrar a venda/baixa de um item da Frente de Caixa
    // POST http://localhost:8080/estoque/venda-item
   
    @PostMapping("/venda-item")
    public ResponseEntity<?> registrarVendaItem(@RequestBody ItemVendaDTO itemVenda) {
        try {
            Produto produtoMovimentado = estoqueService.processarSaidaFrenteCaixa(
                    itemVenda.getCodigoBarras(), 
                    itemVenda.getQuantidade()
            );
            return ResponseEntity.ok().body("Venda realizada com sucesso! Novo estoque de " 
                    + produtoMovimentado.getNome() + ": " + produtoMovimentado.getQuantidadeEstoque());
        } catch (IllegalArgumentException erro) {
            // retorna o erro das validações (Ex: Estoque insuficiente) com status 400
            return ResponseEntity.badRequest().body(erro.getMessage());
        }
    }
}
