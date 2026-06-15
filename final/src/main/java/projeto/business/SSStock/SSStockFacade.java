package projeto.business.SSStock;

import projeto.data.StockDAO;
import projeto.data.SolicitacaoReposicaoDAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import projeto.data.PostoTrabalhoDAO;
import projeto.business.SSStock.Stock;
import projeto.business.SSStock.SolicitacaoReposicao;
import projeto.business.SSStock.PostoTrabalho;
import projeto.business.SSStock.Ingrediente;
import projeto.business.SSStock.StockIngrediente;

public class SSStockFacade implements ISSStock {
    private StockDAO stocks;
    private SolicitacaoReposicaoDAO solicitacoes;
    private PostoTrabalhoDAO postos;

    public SSStockFacade() {
        this.stocks = StockDAO.getInstance();
        this.solicitacoes = SolicitacaoReposicaoDAO.getInstance();
        this.postos = PostoTrabalhoDAO.getInstance();
    }

    private String getIdStock(String idRest) {
        return "STK-" + idRest;
    }

    public SSStockFacade(StockDAO stocks, SolicitacaoReposicaoDAO solicitacoes, PostoTrabalhoDAO postos) {
        this.stocks = stocks;
        this.solicitacoes = solicitacoes;
        this.postos = postos;
    }

    public int verificarStock(String idIng, String idRest) throws IllegalArgumentException {
        String idStock = this.getIdStock(idRest);
        Stock stock = stocks.get(idStock);
        if (stock == null) {
            throw new IllegalArgumentException("Stock inexistente");
        }
        int res = stock.verificarStock(idIng);
        return res;
    }

    public void reporStock(String idRest) {
        String idStock = this.getIdStock(idRest);
        Stock stock = stocks.get(idStock);
        if(stock == null) {
            throw new IllegalArgumentException("Stock inexistente");
        }
        stock.reporStock();
    }
    
    public void reduzStockIngrediente(String idIng, String idRest, int quantidade) throws IllegalArgumentException {
        String idStock = this.getIdStock(idRest);
        Stock stock = stocks.get(idStock);
        if (stock == null) {
            throw new IllegalArgumentException("Stock inexistente");
        }
        stock.reduzStockIngrediente(idIng, quantidade);
    }

    public void reporIngrediente(String idIng, String idRest, int quantidade) {
        String idStock = this.getIdStock(idRest);
        Stock stock = stocks.get(idStock);
        if (stock == null) {
            throw new IllegalArgumentException("Stock inexistente");
        }
        stock.reporIngrediente(idIng, quantidade);
    }

    public String solicitarReposicao(String idIng, String idPosto, int quantidade) throws IllegalArgumentException {
        PostoTrabalho posto = postos.get(idPosto);
        if (posto == null) {
            throw new IllegalArgumentException("Posto de trabalho inexistente");
        }
        String idRest = posto.getIdRestaurante();
        String idStock = this.getIdStock(idRest);
        if (stocks.containsKey(idStock)== false) {
            throw new IllegalArgumentException("Stock inexistente");
        }
        Ingrediente ingrediente = this.getIngrediente(idIng, idRest);

        String idSolicitacao = "SR-" + Integer.toString(solicitacoes.size() + 1);
        SolicitacaoReposicao solicitacao = new SolicitacaoReposicao(idSolicitacao, quantidade, idStock, idPosto, ingrediente);
        solicitacoes.put(idSolicitacao, solicitacao);
        return idSolicitacao;
    }

    public void atualizarDataReposicao(String idSolicitacao, int minutos) throws IllegalArgumentException {
        SolicitacaoReposicao solicitacao = solicitacoes.get(idSolicitacao);
        if (solicitacao == null) {
            throw new IllegalArgumentException("Solicitacao inexistente");
        }
        solicitacao.atualizarDataReposicao(minutos);
        solicitacoes.put(solicitacao.getIdSolicitacao(), solicitacao);
    }

    public void finalizarReposicao(String idSolicitacao) throws IllegalArgumentException {
        SolicitacaoReposicao solicitacao = solicitacoes.get(idSolicitacao);
        if (solicitacao == null) {
            throw new IllegalArgumentException("Solicitacao inexistente");
        }
        solicitacao.finalizarReposicao();
        Stock stock = stocks.get(solicitacao.getIdStock());
        stock.reduzStockIngrediente(solicitacao.getIngrediente().getIdIngrediente(), solicitacao.getQuantidade());
        solicitacoes.put(solicitacao.getIdSolicitacao(), solicitacao);
    }
    
    public List<SolicitacaoReposicao> obterSolicitacoesPendentes(String idRest) {
        String idStock = this.getIdStock(idRest);
        Collection<SolicitacaoReposicao> solicitacoesPendentes = solicitacoes.getSolicitacoesPendentes(idStock);
        List<SolicitacaoReposicao> solicitacoesPendentesList = new ArrayList<>(solicitacoesPendentes);
        solicitacoesPendentesList.sort((s1, s2) -> s1.getDataSolicitacao().compareTo(s2.getDataSolicitacao()));
        return solicitacoesPendentesList;
    }

    public Ingrediente getIngrediente(String idIng, String idRest) {
        String idStock = this.getIdStock(idRest);
        Stock stock = stocks.get(idStock);
        if (stock == null) {
            throw new IllegalArgumentException("Stock inexistente");
        }
        Ingrediente ingrediente = stock.getIngrediente(idIng);
        return ingrediente;
    }

    public long getMinutosAteReposicao(String idSolicitacao) {
        SolicitacaoReposicao solicitacao = solicitacoes.get(idSolicitacao);
        if (solicitacao == null) {
            throw new IllegalArgumentException("Solicitacao inexistente");
        }
        long minutos = solicitacao.getMinutosAteReposicao();
        return minutos;
    }

    public List<StockIngrediente> getStockIngredientes(String idRest) {
        String idStock = this.getIdStock(idRest);
        Stock stock = stocks.get(idStock);
        if (stock == null) {
            throw new IllegalArgumentException("Stock inexistente");
        }
        Collection<StockIngrediente> stockIngredientesCollection = stock.getStockIngredientes();
        List<StockIngrediente> stockIngredientesList = List.copyOf(stockIngredientesCollection);
        return stockIngredientesList;
    }

    public List<PostoTrabalho> getPostosTrabalho(String idRest) {
        Collection<PostoTrabalho> postosCollection = this.postos.values();
        postosCollection.removeIf(p -> !p.getIdRestaurante().equals(idRest));
        List<PostoTrabalho> postosList = List.copyOf(postosCollection);
        return postosList;
    }

    public List<Ingrediente> getIngredientes(String idRestaurante) {
        String idStock = this.getIdStock(idRestaurante);
        Stock stock = stocks.get(idStock);
        if (stock == null) {
            throw new IllegalArgumentException("Stock inexistente");
        }
        List<Ingrediente> ingredientesList =  stock.getIngredientes();
        return ingredientesList;
    }
}
