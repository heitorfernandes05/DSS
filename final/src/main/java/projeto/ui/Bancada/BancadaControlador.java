package projeto.ui.Bancada;

import java.util.ArrayList;
import java.util.List;

import projeto.business.ILNFacade;
import projeto.business.SSPedido.Pedido;
import projeto.business.SSStock.Ingrediente;

public class BancadaControlador {
    private final ILNFacade lnFacade;
    private final String idRestaurante;
    private String idPosto;
    private boolean autenticado;

    public BancadaControlador(String idRestaurante, String idPosto,  ILNFacade facade){
        this.lnFacade = facade;
        this.idRestaurante = String.valueOf(idRestaurante);
        this.idPosto = String.valueOf(idPosto);
        this.autenticado = true;
    }

    public BancadaControlador(String idRestaurante, ILNFacade facade) {
        this.lnFacade = facade;
        this.idRestaurante = idRestaurante;
        this.idPosto = null;
        this.autenticado = false;
    }

    public String obterPostoTrabalho() {
        return this.idPosto;
    }

    public String solicitarReposicao(String idIngrediente, int quantidade) {
        if (!this.autenticado || this.idPosto == null || this.idPosto.isBlank()) {
            return null;
        }
        if (idIngrediente == null || idIngrediente.isBlank() || quantidade <= 0) {
            return null;
        }
        try {
            return this.lnFacade.solicitarReposicao(idIngrediente, this.idPosto, quantidade);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public long obterMinutosAteReposicao(String idSolicitacao) {
        if (idSolicitacao == null || idSolicitacao.isBlank()) {
            return -1;
        }
        try {
            return this.lnFacade.obterMinutosAteReposicao(idSolicitacao);
        } catch (RuntimeException e) {
            return -1;
        }
    }

    public boolean atrasarPedido(int idPedido, int minutos) {
        if (!this.autenticado || this.idPosto == null || this.idPosto.isBlank() || idPedido <= 0 || minutos <= 0) {
            return false;
        }
        this.lnFacade.atrasarPedido(idPedido, minutos);
        return true;
    }

    public List<String> obterPedidosPendentes() {
        List<String> pedidosTexto = new ArrayList<>();
        List<Pedido> pedidos = this.lnFacade.obterPedidosPendentes(this.idRestaurante);
        for (Pedido p : pedidos) {
            String pedidoInfo = p.getNumero() + " - Feito a: " + p.getTimestamp().toString() + ", Tempo Estimado: " + p.getTempoPreparacao() + " min, Estado: " + p.getEstado();
            pedidoInfo += "\nProdutos:";
            for (int i = 0; i < p.getProdutosSelecionados().size(); i++) {
                if (i > 0)
                    pedidoInfo += ",";
                pedidoInfo =  p.getProdutosSelecionados().get(i).getNome();
            }
            pedidosTexto.add(pedidoInfo);
        }
        return pedidosTexto;
    }

    public List<String> obterPedidosPendentesEstado() {
        List<String> pedidosTexto = new ArrayList<>();
        List<Pedido> pedidos = this.lnFacade.obterPedidosPendentes(this.idRestaurante);
        for (Pedido p : pedidos) {
            String pedidoInfo = p.getNumero() + "-" + p.getEstado();
            pedidosTexto.add(pedidoInfo);
        }
        return pedidosTexto;
    }

    public boolean atualizarEstadoPedido(int idPedido, String estadoAtual) {
        String novoEstado = "";
        if(estadoAtual.equals("pago")) {
            novoEstado = "em preparação";
        } else if(estadoAtual.equals("em preparação")) {
            novoEstado = "concluído";
        } else {
            return false;
        }
        try {
            this.lnFacade.atualizarEstado(idPedido, novoEstado);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public List<String> obterIngredientes() {
        List<Ingrediente> ingredientes = this.lnFacade.getIngredientes(this.idRestaurante);
        List<String> ingredientesTexto = new ArrayList<>();
        for (Ingrediente ing : ingredientes) {
            String ingInfo = ing.getIdIngrediente() + " - " + ing.getNome();
            ingredientesTexto.add(ingInfo);
        }
        return ingredientesTexto;
    }

}
