package io.github.jonathanSampaio.rest.controller;

import io.github.jonathanSampaio.domain.entity.ItemPedido;
import io.github.jonathanSampaio.domain.entity.Pedido;
import io.github.jonathanSampaio.domain.enums.StatusPedido;
import io.github.jonathanSampaio.domain.service.PedidoService;
import io.github.jonathanSampaio.rest.dto.AtualizacaoStatusPedidoDTO;
import io.github.jonathanSampaio.rest.dto.InformacaoItemPedidoDTO;
import io.github.jonathanSampaio.rest.dto.InformacoesPedidoDTO;
import io.github.jonathanSampaio.rest.dto.PedidoDTO;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }


    @PostMapping
    @ResponseStatus(CREATED)
    public Integer save(@RequestBody PedidoDTO pedidoDTO){
        Pedido pedido = pedidoService.salvar(pedidoDTO);

        return pedido.getId();
    }

    @GetMapping("{id}")
    public InformacoesPedidoDTO getById(@PathVariable Integer id){
        return pedidoService.obterPedidoCompleto(id)
                .map( p -> converter(p))
                .orElseThrow(() ->
                        new ResponseStatusException(NOT_FOUND, "Pedido não encontrado "));
    }

    @PatchMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void updateStatus(@PathVariable Integer id,
                             @RequestBody AtualizacaoStatusPedidoDTO dto){
        String novoStatus = dto.getNovoStatus();
        pedidoService.atualizaStatus(id, StatusPedido.valueOf(novoStatus));

    }

    private InformacoesPedidoDTO converter(Pedido pedido){
        return InformacoesPedidoDTO.builder()
                .codigo(pedido.getId())
                .dataPedido(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .cpf(pedido.getCliente().getCpf())
                .nomeCliente(pedido.getCliente().getNome())
                .total(pedido.getTotal())
                .status(pedido.getStatus().name())
                .itens(converter(pedido.getItens()))
                .build();

    }

    private List<InformacaoItemPedidoDTO> converter(List<ItemPedido> itens){
        if (CollectionUtils.isEmpty(itens)){
            return Collections.emptyList();
        }

        return itens.stream()
                .map(item ->
                        InformacaoItemPedidoDTO.builder().descricaoProduto(item.getProduto().getDescricao())
                                .precoUnitario(item.getProduto().getPreco())
                                .quantidade(item.getQuantidade())
                                .build()
                ).collect(Collectors.toList());
    }


    @GetMapping
    public List<Pedido> buscar(){
        return pedidoService.buscarTodos();
    }
}




//    @PostMapping
//    @ResponseStatus(CREATED)
//    public Boolean save(@RequestBody PedidoDTO pedidoDTO){
//        Boolean pedido = pedidoService.salvarPedido(pedidoDTO);
//
//        return true;
//    }