package projeto.business.SSPedido;

import java.util.List;
import projeto.data.PedidoDAO;
import projeto.data.ProdutoDAO;

public class SSPedidoFacade implements ISSPedido{
    private PedidoDAO pedidos;
    private ProdutoDAO produtos;

    public SSPedidoFacade() {
        this.pedidos = PedidoDAO.getInstance();
        this.produtos = ProdutoDAO.getInstance();
    }

    @Override
    public int criarPedido(String idRestaurante) {
        int numero;
        for (numero = 1; this.pedidos.containsKey(numero); numero++);
        Pedido pedido = new Pedido(numero, idRestaurante);
        this.pedidos.put(numero, pedido);
        return numero;
    }

    @Override
    public boolean adicionarProduto(int idPedido, String idProduto, int quantidade) {
        if (!this.pedidos.containsKey(idPedido)) {
            return false;
        }
        Pedido pedido = this.pedidos.get(idPedido);
        if (!this.produtos.containsKey(idProduto)) {
            return false;
        }
        Produto produto = this.produtos.get(idProduto);
        for (int i = 0; i < quantidade; i++) {
            if (!pedido.addProduto(produto)) {
                return false;
            }
        }
        this.pedidos.put(idPedido, pedido);
        return true;
    }

    @Override
    public void removerProduto(int idPedido, int idProdutoPedido) {
        if (!this.pedidos.containsKey(idPedido)) {
            return;
        }
        Pedido pedido = this.pedidos.get(idPedido);
        pedido.removeProduto(idProdutoPedido);
        this.pedidos.put(idPedido, pedido);
    }

    @Override
    public String obterProdutosSelecionadosTexto(int idPedido) {
        if (!this.pedidos.containsKey(idPedido)) {
            return "";
        }
        Pedido pedido = this.pedidos.get(idPedido);
        return pedido.produtosToString();
    }
    
    public String obterFatura(int idPedido) {
        if (!this.pedidos.containsKey(idPedido)) {
            return null;
        }
        Pedido pedido = this.pedidos.get(idPedido);
        Pagamento pagamento = pedido.getPagamento();
        Fatura fatura = pagamento == null ? null : pagamento.getFatura();
        return fatura == null ? null : fatura.toString();
    }

    @Override
    public float obterTotalPedido(int idPedido) {
        if (!this.pedidos.containsKey(idPedido)) {
            return 0;
        }
        Pedido pedido = this.pedidos.get(idPedido);
        return pedido.getPrecoTotal();
    }

    @Override
    public void removerIngredienteItem(int idPedido, int idProdutoPedido, int idItem, String idIngrediente) {
        if (!this.pedidos.containsKey(idPedido)) {
            return;
        }
        Pedido pedido = this.pedidos.get(idPedido);
        pedido.removerIngrediente(idProdutoPedido, idItem, idIngrediente);
        this.pedidos.put(idPedido, pedido);
    }

    @Override
    public void adicionarNota(int idPedido, String texto) {
        if (!this.pedidos.containsKey(idPedido)) {
            return;
        }
        Pedido pedido = this.pedidos.get(idPedido);
        pedido.addNota(texto);
        this.pedidos.put(idPedido, pedido);
    }

    @Override
    public void definirLocalEntrega(int idPedido, String local) {
        if (!this.pedidos.containsKey(idPedido)) {
            return;
        }
        Pedido pedido = this.pedidos.get(idPedido);
        pedido.setLocalEntrega(local);
        this.pedidos.put(idPedido, pedido);
    }

    @Override
    public void cancelarPedido(int idPedido) {
        if (!this.pedidos.containsKey(idPedido)) {
            return;
        }
        Pedido pedido = this.pedidos.get(idPedido);
        pedido.cancelar();
        this.pedidos.put(idPedido, pedido);
    }

    @Override
    public void confirmarPedido(int idPedido) {
        if (!this.pedidos.containsKey(idPedido)) {
            return;
        }
        Pedido pedido = this.pedidos.get(idPedido);
        pedido.submeter();
        this.pedidos.put(idPedido, pedido);
    }

    @Override
    public boolean efetuarPagamento(int idPedido, String tipo, float valor) {
        if (!this.pedidos.containsKey(idPedido)) {
            return false;
        }
        Pedido pedido = this.pedidos.get(idPedido);
        boolean res = pedido.pagar(valor, tipo);
        this.pedidos.put(idPedido, pedido);
        return res;
    }

    @Override
    public void atrasarPedido(int idPedido, int tempoAtraso) {
        if (!this.pedidos.containsKey(idPedido)) {
            return;
        }
        Pedido pedido = this.pedidos.get(idPedido);
        pedido.atrasar(tempoAtraso);
        this.pedidos.put(idPedido, pedido);
    }

    @Override
    public void atualizarEstado(int idPedido, String estado) {
        if (!this.pedidos.containsKey(idPedido)) {
            return;
        }
        Pedido pedido = this.pedidos.get(idPedido);
        pedido.atualizarEstado(estado);
        this.pedidos.put(idPedido, pedido);
    }
    
    @Override
    public List<Pedido> getPedidos() {
        return this.pedidos.values().stream().map(Pedido::clone).toList();
    }

    @Override
    public List<Produto> getProdutos() {
        return this.produtos.values().stream().map(Produto::clone).toList();
    }

    @Override
    public List<Pedido> obterPedidosPendentes(String idRestaurante) {
        return this.pedidos.values().stream()
                .filter(p -> p.getRestaurante().equals(idRestaurante) && (p.getEstado().equals("pago") || p.getEstado().equals("em preparação")))
                .map(Pedido::clone)
                .toList();
    }
}
