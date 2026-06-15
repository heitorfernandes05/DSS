package projeto.business;

import java.time.Duration;
import java.util.List;

import projeto.business.SSAdministracao.Restaurante;
import projeto.business.SSPedido.Pedido;
import projeto.business.SSPedido.Produto;
import projeto.business.SSStock.Ingrediente;
import projeto.business.SSStock.PostoTrabalho;
import projeto.business.SSStock.SolicitacaoReposicao;
import projeto.business.SSStock.StockIngrediente;

public interface ILNFacade {
    public void autenticar(String email, String password) throws IllegalArgumentException;
    void terminarSessao();

    String getCargoUtilizador();
    List<Restaurante> getRestaurantesAcessiveis();
    List<Restaurante> getAllRestaurantes();
    Duration getTempoMedioAtendimento(String idRestaurante);
    double getFaturacao(String idRestaurante);

    void enviarMensagem(String idRestaurante, String mensagem);

    int criarPedido(String idRestaurante);
    public List<Produto> obterProdutosDisponiveis(String idRestaurante) throws IllegalArgumentException;
    boolean adicionarProduto(int idPedido, String idProduto, int quantidade);
    void removerProduto(int idPedido, int idProdutoPedido);
    String obterProdutosSelecionadosTexto(int idPedido);
    String obterFatura(int idPedido);
    float obterTotalPedido(int idPedido);
    void removerIngredienteItem(int idPedido, int idProdutoPedido, int idItem, String idIngrediente);
    void adicionarNota(int idPedido, String texto);
    void definirLocalEntrega(int idPedido, String local);
    void confirmarPedido(int idPedido);
    boolean efetuarPagamento(int idPedido, String tipo, float valor);
    void cancelarPedido(int idPedido);

    String solicitarReposicao(String idIngrediente, String idPosto, int quantidade);
    long obterMinutosAteReposicao(String idSolicitacao);
    void atrasarPedido(int idPedido, int tempoAtraso);
    public List<Pedido> obterPedidosPendentes(String idRestaurante);
    public List<PostoTrabalho> getPostosTrabalho(String idRest);

    int verificarStock(String idIng, String idRest);
    void reporStock(String idRest);
    void reduzStockIngrediente(String idIng, String idRest, int quantidade);
    void reporIngrediente(String idIng, String idRest, int quantidade);
    void atualizarDataReposicao(String idSolicitacao, int minutos);
    void finalizarReposicao(String idSolicitacao);
    List<SolicitacaoReposicao> obterSolicitacoesPendentes(String idRest);
    Ingrediente getIngrediente(String idIng, String idRest);
    long getMinutosAteReposicao(String idSolicitacao);

    public void atualizarEstado(int idPedido, String estado);
    public List<Ingrediente> getIngredientes(String idRestaurante);

    public List<StockIngrediente> getStockIngredientes(String idRest);
}