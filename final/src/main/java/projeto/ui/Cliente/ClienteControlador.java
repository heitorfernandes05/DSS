package projeto.ui.Cliente;

import java.util.List;

import projeto.business.ILNFacade;
import projeto.business.SSPedido.Item;
import projeto.business.SSPedido.Menu;
import projeto.business.SSPedido.Produto;

public class ClienteControlador {
    private final ILNFacade lnFacade;
    private final String idRestaurante;
    private int pedidoAtual;

    public ClienteControlador(String idRestaurante, ILNFacade facade) {
        this.lnFacade = facade;
        this.idRestaurante = idRestaurante;
        this.pedidoAtual = -1;
    }

    public int iniciarPedido() {
        this.pedidoAtual = this.lnFacade.criarPedido(this.idRestaurante);
        return this.pedidoAtual;
    }

    public String obterProdutosDisponiveisTexto() {
        try {
            List<Produto> produtos = this.lnFacade.obterProdutosDisponiveis(idRestaurante);
            
            if (produtos.isEmpty()) {
                return "Nenhum produto disponivel.\n";
            }
            
            produtos.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
            
            StringBuilder sb = new StringBuilder();
            sb.append("PRODUTOS DISPONIVEIS\n");
            sb.append("====================\n");
            
            for (Produto p : produtos) {
                sb.append(p.getId()).append(" - ") 
                .append(p.getNome())
                .append(" - ")
                .append(String.format("%.2f", p.getPrecoBase()))
                .append(" EUR");
                
                if (p instanceof Menu) {
                    Menu menu = (Menu) p;
                    float precoComDesconto = menu.getPrecoBase() * (1 - menu.getDesconto() / 100.0f);
                    sb.append(" (Menu - ")
                    .append(String.format("%.2f", precoComDesconto))
                    .append(" EUR");
                    sb.append("\n     Itens incluidos:");
                    for (Item item : menu.getItens()) {
                        sb.append("\n       - ").append(item.getId())
                        .append(": ").append(item.getNome());
                    }
                    
                } else {
                    Item item = (Item) p;
                    sb.append(" (")
                    .append(item.getTempoPreparacao())
                    .append(" min)");
                }
                
                sb.append("\n");
            }
            
            sb.append("====================\n");
            sb.append("Total: ").append(produtos.size()).append(" produto(s)\n");
            return sb.toString();
            
        } catch (Exception e) {
            return "Erro: " + e.getMessage();
        }
    }

    public String obterProdutosSelecionadosTexto() {
        garantirPedidoAtivo();
        return this.lnFacade.obterProdutosSelecionadosTexto(this.pedidoAtual);
    }

    public boolean adicionarProduto(String idProduto, int quantidade) {
        garantirPedidoAtivo();
        if (quantidade <= 0) {
            return false;
        }
        return this.lnFacade.adicionarProduto(this.pedidoAtual, idProduto, quantidade);
    }

    public void removerProduto(int idProdutoPedido) {
        garantirPedidoAtivo();
        this.lnFacade.removerProduto(this.pedidoAtual, idProdutoPedido);
    }

    public void removerIngredienteItem(int idProdutoPedido, int idItem, String idIngrediente) {
        garantirPedidoAtivo();
        this.lnFacade.removerIngredienteItem(this.pedidoAtual, idProdutoPedido, idItem, idIngrediente);
    }

    public void adicionarNota(String texto) {
        garantirPedidoAtivo();
        if (texto == null || texto.isBlank()) {
            return;
        }
        this.lnFacade.adicionarNota(this.pedidoAtual, texto);
    }

    public void definirLocalEntrega(String local) {
        garantirPedidoAtivo();
        this.lnFacade.definirLocalEntrega(this.pedidoAtual, local);
    }

    public void confirmarPedido() {
        garantirPedidoAtivo();
        this.lnFacade.confirmarPedido(this.pedidoAtual);
    }

    public boolean efetuarPagamento(String tipo, float valor) {
        garantirPedidoAtivo();
        try {
            return this.lnFacade.efetuarPagamento(this.pedidoAtual, tipo, valor);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void cancelarPedido() {
        if (this.pedidoAtual < 0) {
            return;
        }
        this.lnFacade.cancelarPedido(this.pedidoAtual);
        this.pedidoAtual = -1;
    }

    public float calcularTotal() {
        garantirPedidoAtivo();
        return this.lnFacade.obterTotalPedido(this.pedidoAtual);
    }

    public String obterFatura() {
        garantirPedidoAtivo();
        return this.lnFacade.obterFatura(this.pedidoAtual);
    }

    public boolean temProdutosSelecionados() {
        return !obterProdutosSelecionadosTexto().isBlank();
    }

    private void garantirPedidoAtivo() {
        if (this.pedidoAtual < 0) {
            throw new IllegalStateException("Nenhum pedido ativo.");
        }
    }
}
