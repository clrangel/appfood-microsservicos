package br.com.appfood.ms_pagamentos.dto;

import br.com.appfood.ms_pagamentos.model.Status;

public record AutorizacaoDto(Long idPedido,
                             Status status) {
}
