package br.com.appfood.ms_pagamentos.repository;

import br.com.appfood.ms_pagamentos.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}
