package br.com.appfodd.ms_pedidos.dto;

import br.com.appfodd.ms_pedidos.model.Status;

public record AutorizacaoDto(Long idPedido,
                             Status status) {
}
