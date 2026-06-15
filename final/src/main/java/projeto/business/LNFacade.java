package projeto.business;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import projeto.business.SSAdministracao.ISSAdministracao;
import projeto.business.SSAdministracao.Restaurante;
import projeto.business.SSAdministracao.SSAdministracaoFacade;
import projeto.business.SSPedido.ISSPedido;
import projeto.business.SSPedido.Produto;
import projeto.business.SSPedido.Pedido;
import projeto.business.SSPedido.Item;
import projeto.business.SSPedido.Menu;
import projeto.business.SSPedido.SSPedidoFacade;
import projeto.business.SSStock.ISSStock;
import projeto.business.SSStock.Ingrediente;
import projeto.business.SSStock.PostoTrabalho;
import projeto.business.SSStock.SSStockFacade;
import projeto.business.SSStock.SolicitacaoReposicao;
import projeto.business.SSStock.StockIngrediente;

public class LNFacade implements ILNFacade {
    
    private ISSAdministracao ssAdministracao;
    private ISSPedido ssPedido;
    private ISSStock ssStock;

    public LNFacade() {
        this.ssAdministracao = new SSAdministracaoFacade();
        this.ssPedido = new SSPedidoFacade();
        this.ssStock = new SSStockFacade();
    }

    @Override
    public void autenticar(String email, String password) throws IllegalArgumentException {
        this.ssAdministracao.autenticar(email, password);
    }

    @Override
    public void terminarSessao() {
        this.ssAdministracao.terminarSessao();
    }

    @Override
    public String getCargoUtilizador() {
        return this.ssAdministracao.getCargoUtilizador();
    }

    @Override
    public List<Restaurante> getRestaurantesAcessiveis() {
        return this.ssAdministracao.getRestaurantesAcessiveis();
    }

    @Override
    public List<Restaurante> getAllRestaurantes() {
        return this.ssAdministracao.getAllRestaurantes();
    }

    @Override
    public Duration getTempoMedioAtendimento(String idRest) {
        return this.ssAdministracao.getTempoMedioAtendimento(idRest);
    }

    @Override
    public double getFaturacao(String idRest) {
        return this.ssAdministracao.getFaturacao(idRest);
    }

    @Override
    public void enviarMensagem(String idRestaurante, String mensagem) {
        this.ssAdministracao.enviarMensagem(idRestaurante, mensagem);
    }

    @Override
    public int criarPedido(String idRestaurante) {
        return this.ssPedido.criarPedido(idRestaurante);
    }

    @Override
    public List<Produto> obterProdutosDisponiveis(String idRestaurante) throws IllegalArgumentException {
        List<Produto> todosProdutos = this.ssPedido.getProdutos();
        List<Produto> produtosDisponiveis = new ArrayList<>();
        
        for (Produto produto : todosProdutos) {
            boolean disponivel = true;
            
            if (produto instanceof Menu) {
                Menu menu = (Menu) produto;
                for (Item item : menu.getItens()) {
                    for (Ingrediente ingrediente : item.getIngredientes()) {
                        if (this.ssStock.verificarStock(ingrediente.getIdIngrediente(), idRestaurante) <= 0) {
                            disponivel = false;
                            break;
                        }
                    }
                    if (!disponivel) break;
                }
            } else if (produto instanceof Item) {
                Item item = (Item) produto;
                for (Ingrediente ingrediente : item.getIngredientes()) {
                    if (this.ssStock.verificarStock(ingrediente.getIdIngrediente(), idRestaurante) <= 0) {
                        disponivel = false;
                        break;
                    }
                }
            } else {
                disponivel = false;
            }
            
            if (disponivel) {
                produtosDisponiveis.add(produto);
            }
        }
        
        return produtosDisponiveis;
    }

    @Override
    public boolean adicionarProduto(int idPedido, String idProduto, int quantidade) {
        return this.ssPedido.adicionarProduto(idPedido, idProduto, quantidade);
    }

    @Override
    public void removerProduto(int idPedido, int idProdutoPedido) {
        this.ssPedido.removerProduto(idPedido, idProdutoPedido);
    }

    @Override
    public String obterProdutosSelecionadosTexto(int idPedido) {
        return this.ssPedido.obterProdutosSelecionadosTexto(idPedido);
    }

    @Override
    public String obterFatura(int idPedido) {
        return this.ssPedido.obterFatura(idPedido);
    }

    @Override
    public float obterTotalPedido(int idPedido) {
        return this.ssPedido.obterTotalPedido(idPedido);
    }

    @Override
    public void removerIngredienteItem(int idPedido, int idProdutoPedido, int idItem, String idIngrediente) {
        this.ssPedido.removerIngredienteItem(idPedido, idProdutoPedido, idItem, idIngrediente);
    }

    @Override
    public void adicionarNota(int idPedido, String texto) {
        this.ssPedido.adicionarNota(idPedido, texto);
    }

    @Override
    public void definirLocalEntrega(int idPedido, String local) {
        this.ssPedido.definirLocalEntrega(idPedido, local);
    }

    @Override
    public void confirmarPedido(int idPedido) {
        this.ssPedido.confirmarPedido(idPedido);
    }

    @Override
    public boolean efetuarPagamento(int idPedido, String tipo, float valor) {
        return this.ssPedido.efetuarPagamento(idPedido, tipo, valor);
    }

    @Override
    public void cancelarPedido(int idPedido) {
        this.ssPedido.cancelarPedido(idPedido);
    }

    @Override
    public String solicitarReposicao(String idIngrediente, String idPosto, int quantidade) {
        return this.ssStock.solicitarReposicao(idIngrediente, idPosto, quantidade);
    }

    @Override
    public long obterMinutosAteReposicao(String idSolicitacao) {
        return this.ssStock.getMinutosAteReposicao(idSolicitacao);
    }

    @Override
    public void atrasarPedido(int idPedido, int tempoAtraso) {
        this.ssPedido.atrasarPedido(idPedido, tempoAtraso);
    }

    @Override
    public List<Pedido> obterPedidosPendentes(String idRestaurante) {
        return this.ssPedido.obterPedidosPendentes(idRestaurante);
    }

    @Override
    public List<PostoTrabalho> getPostosTrabalho(String idRest) {
        return this.ssStock.getPostosTrabalho(idRest);
    }

    @Override
    public void atualizarEstado(int idPedido, String estado) {
        this.ssPedido.atualizarEstado(idPedido, estado);
    }

    @Override
    public List<Ingrediente> getIngredientes(String idRestaurante) {
        return this.ssStock.getIngredientes(idRestaurante);
    }

    @Override
    public int verificarStock(String idIng, String idRest) {
        return this.ssStock.verificarStock(idIng, idRest);
    }

    @Override
    public void reporStock(String idRest) {
        this.ssStock.reporStock(idRest);
    }

    @Override
    public void reduzStockIngrediente(String idIng, String idRest, int quantidade) {
        this.ssStock.reduzStockIngrediente(idIng, idRest, quantidade);
    }

    @Override
    public void reporIngrediente(String idIng, String idRest, int quantidade) {
        this.ssStock.reporIngrediente(idIng, idRest, quantidade);
    }

    @Override
    public void atualizarDataReposicao(String idSolicitacao, int minutos) {
        this.ssStock.atualizarDataReposicao(idSolicitacao, minutos);
    }

    @Override
    public void finalizarReposicao(String idSolicitacao) {
        this.ssStock.finalizarReposicao(idSolicitacao);
    }

    @Override
    public List<SolicitacaoReposicao> obterSolicitacoesPendentes(String idRest) {
        return this.ssStock.obterSolicitacoesPendentes(idRest);
    }

    @Override
    public Ingrediente getIngrediente(String idIng, String idRest) {
        return this.ssStock.getIngrediente(idIng, idRest);
    }

    @Override
    public long getMinutosAteReposicao(String idSolicitacao) {
        return this.ssStock.getMinutosAteReposicao(idSolicitacao);
    }

    @Override   
    public List<StockIngrediente> getStockIngredientes(String idRest) {
        return this.ssStock.getStockIngredientes(idRest);
    }
}


