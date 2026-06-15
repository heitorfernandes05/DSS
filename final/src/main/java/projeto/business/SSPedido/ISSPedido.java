package projeto.business.SSPedido;

import java.util.List;

public interface ISSPedido {
    
    public int criarPedido(String idRestaurante);

    public boolean adicionarProduto(int idPedido, String idProduto, int quantidade);
    
    public void removerProduto(int idPedido, int idProdutoPedido);

    public String obterProdutosSelecionadosTexto(int idPedido);

    public String obterFatura(int idPedido);

    public float obterTotalPedido(int idPedido);
    
    public void removerIngredienteItem(int idPedido, int idProdutoPedido, int idItem, String idIngrediente);
    
    public void adicionarNota(int idPedido, String texto);
    
    public void definirLocalEntrega(int idPedido, String local);
    
    public void cancelarPedido(int idPedido);
    
    public void confirmarPedido(int idPedido);
    
    public boolean efetuarPagamento(int idPedido, String tipo, float valor);
    
    public void atrasarPedido(int idPedido, int tempoAtraso);
    
    public void atualizarEstado(int idPedido, String estado);

    public List<Pedido> getPedidos();

    public List<Produto> getProdutos();
    
    public List<Pedido> obterPedidosPendentes(String idRestaurante);
}
