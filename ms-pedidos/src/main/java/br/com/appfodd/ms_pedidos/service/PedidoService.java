package br.com.appfodd.ms_pedidos.service;

import br.com.appfodd.ms_pedidos.dto.AutorizacaoDto;
import br.com.appfodd.ms_pedidos.dto.PedidoDto;
import br.com.appfodd.ms_pedidos.dto.StatusDto;
import br.com.appfodd.ms_pedidos.model.Pedido;
import br.com.appfodd.ms_pedidos.model.Status;
import br.com.appfodd.ms_pedidos.repository.PedidoRepository;
import br.com.appfodd.ms_pedidos.utils.AutorizacaoPagamentoClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private final AutorizacaoPagamentoClient client;

    public List<PedidoDto> obterTodos() {
        return repository.findAll().stream()
                .map(p -> modelMapper.map(p, PedidoDto.class))
                .collect(Collectors.toList());
    }

    public PedidoDto obterPorId(Long id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        return modelMapper.map(pedido, PedidoDto.class);
    }

    private Status obterStatusPagamento(String id) {
        AutorizacaoDto autorizacao = client.obterAutorizacao(id);
        if (autorizacao.status().equals("autorizado")) {
            return Status.PREPARANDO;
        }

        return Status.NAO_AUTORIZADO;
    }

    public PedidoDto criarPedido(PedidoDto dto, Boolean comErro) {
        Pedido pedido = modelMapper.map(dto, Pedido.class);

        Status status = Status.AGUARDANDO_PAGAMENTO;

        pedido.setDataHora(LocalDateTime.now());
        pedido.setStatus(Status.REALIZADO);
        pedido.getItens().forEach(item -> item.setPedido(pedido));
        Pedido salvo = repository.save(pedido);
        status = obterStatusPagamento(pedido.getId().toString());
        if (comErro) {
            status = Status.ERRO_CONSULTA_PGTO;
        } else {
            status = obterStatusPagamento(pedido.getId().toString());
        }

        pedido.setStatus(status);
        repository.save(pedido);

        return modelMapper.map(pedido, PedidoDto.class);
    }

    public PedidoDto atualizaStatus(Long id, StatusDto dto) {

        Pedido pedido = repository.porIdComItens(id);

        if (pedido == null) {
            throw new EntityNotFoundException();
        }

        pedido.setStatus(dto.getStatus());
        repository.atualizaStatus(dto.getStatus(), pedido);
        return modelMapper.map(pedido, PedidoDto.class);
    }

    public void aprovaPagamentoPedido(Long id) {

        Pedido pedido = repository.porIdComItens(id);

        if (pedido == null) {
            throw new EntityNotFoundException();
        }

        pedido.setStatus(Status.PAGO);
        repository.atualizaStatus(Status.PAGO, pedido);
    }
}
