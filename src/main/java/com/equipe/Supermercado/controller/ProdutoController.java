
package com.equipe.Supermercado.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equipe.Supermercado.model.Produto;
import com.equipe.Supermercado.service.ProdutoService;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Produto produto) {

        try {
            Produto novo = service.cadastrarProduto(produto);
            return ResponseEntity.status(201).body(novo);

        } catch (IllegalArgumentException erro) {
            return ResponseEntity.badRequest().body(erro.getMessage());
        }
    }
}
