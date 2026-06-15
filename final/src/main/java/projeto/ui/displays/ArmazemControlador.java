package projeto.ui.displays;

import java.util.List;
import projeto.business.ILNFacade;
import projeto.business.SSStock.SolicitacaoReposicao;
import projeto.business.SSStock.StockIngrediente;
import projeto.business.SSStock.Ingrediente;

public class ArmazemControlador {
    private final ILNFacade lnFacade;
    private final String idRestaurante;

    public ArmazemControlador(int idRestaurante, ILNFacade facade) {
        this(Integer.toString(idRestaurante), facade);
    }

    public ArmazemControlador(String idRestaurante, ILNFacade facade) {
        this.lnFacade = facade;
        this.idRestaurante = idRestaurante;
    }

    public int verificarStock(String idIngrediente) {
        return this.lnFacade.verificarStock(idIngrediente, this.idRestaurante);
    }

    public void reporStockTotal() {
        this.lnFacade.reporStock(this.idRestaurante);
    }

    public void reduzirStockIngrediente(String idIngrediente, int quantidade) {
        this.lnFacade.reduzStockIngrediente(idIngrediente, this.idRestaurante, quantidade);
    }

    public void reporIngrediente(String idIngrediente, int quantidade) {
        this.lnFacade.reporIngrediente(idIngrediente, this.idRestaurante, quantidade);
    }

    public String solicitarReposicao(String idIngrediente, String idPosto, int quantidade) {
        return this.lnFacade.solicitarReposicao(idIngrediente, idPosto, quantidade);
    }

    public void atualizarDataReposicao(String idSolicitacao, int minutos) {
        this.lnFacade.atualizarDataReposicao(idSolicitacao, minutos);
    }

    public void finalizarReposicao(String idSolicitacao) {
        this.lnFacade.finalizarReposicao(idSolicitacao);
    }

    public List<SolicitacaoReposicao> obterSolicitacoesPendentes() {
        return this.lnFacade.obterSolicitacoesPendentes(this.idRestaurante);
    }

    public Ingrediente obterIngrediente(String idIngrediente) {
        return this.lnFacade.getIngrediente(idIngrediente, this.idRestaurante);
    }

    public long getMinutosAteReposicao(String idSolicitacao) {
        return this.lnFacade.getMinutosAteReposicao(idSolicitacao);
    }

    public List<String> verificarStock() {
        List<StockIngrediente> stockIngredientes = this.lnFacade.getStockIngredientes(this.idRestaurante);
        return stockIngredientes.stream()
                .map(si ->  si.getIngrediente().getIdIngrediente() + "- Nome:" + si.getIngrediente().getNome() + ", Quantidade: " + si.getQuantidade())
                .toList();
    }
}
