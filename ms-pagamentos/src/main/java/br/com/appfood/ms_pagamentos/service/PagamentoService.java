package br.com.appfood.ms_pagamentos.service;

import br.com.appfood.ms_pagamentos.dto.AutorizacaoDto;
import br.com.appfood.ms_pagamentos.dto.PagamentoDto;
import br.com.appfood.ms_pagamentos.model.GeradorAutorizacao;
import br.com.appfood.ms_pagamentos.model.Pagamento;
import br.com.appfood.ms_pagamentos.model.Status;
import br.com.appfood.ms_pagamentos.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository repository;

    @Autowired
    private ModelMapper modelMapper;


    public Page<PagamentoDto> obterTodos(Pageable paginacao) {
        return repository
                .findAll(paginacao)
                .map(p -> modelMapper.map(p, PagamentoDto.class));
    }

    public PagamentoDto obterPorId(Long id) {
        Pagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public AutorizacaoDto autorizarPagamento(Long id) {
        Pagamento pagamento = new Pagamento();
        pagamento.setPedidoId(id);
        Status status = Status.valueOf(GeradorAutorizacao.getRandomBoolean() ? "Autorizado" : "Recusado");
        pagamento.setStatus(status);
        repository.save(pagamento);
        return new AutorizacaoDto(pagamento.getId(), pagamento.getStatus());
    }

    public PagamentoDto criarPagamento(PagamentoDto dto) {
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        repository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public PagamentoDto atualizarPagamento(Long id, PagamentoDto dto) {
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setId(id);
        pagamento = repository.save(pagamento);
        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public void excluirPagamento(Long id) {
        repository.deleteById(id);
    }

}
