package com.equipe.Supermercado.repository;

import com.equipe.Supermercado.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Método extra para a sua equipa conseguir procurar um produto pelo código de barras no momento da venda!
    Optional<Produto> findByCodigoBarras(String codigoBarras);
}