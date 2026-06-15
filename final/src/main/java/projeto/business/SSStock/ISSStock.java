package projeto.business.SSStock;

import java.util.List;

public interface ISSStock {
    public int verificarStock(String idIng, String idRest) throws IllegalArgumentException;

    public void reporStock(String idRest);

    public void reduzStockIngrediente(String idIng, String idRest, int quantidade) throws IllegalArgumentException;

    public void reporIngrediente(String idIng, String idRest, int quantidade);

    public String solicitarReposicao(String idIng, String idPosto, int quantidade) throws IllegalArgumentException;

    public void atualizarDataReposicao(String idSolicitacao, int minutos);

    public void finalizarReposicao(String idSolicitacao) throws IllegalArgumentException;
    
    public List<SolicitacaoReposicao> obterSolicitacoesPendentes(String idRest);

    public Ingrediente getIngrediente(String idIng, String idRest);

    public long getMinutosAteReposicao(String idSolicitacao);

    public List<PostoTrabalho> getPostosTrabalho(String idRest);
    public List<Ingrediente> getIngredientes(String idRestaurante);

    public List<StockIngrediente> getStockIngredientes(String idRest);
}
