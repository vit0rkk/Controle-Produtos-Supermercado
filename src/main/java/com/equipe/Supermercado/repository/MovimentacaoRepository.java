package com.equipe.Supermercado.repository;

import com.equipe.Supermercado.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    // O Spring já inclui os métodos save() e findAll() automaticamente
}