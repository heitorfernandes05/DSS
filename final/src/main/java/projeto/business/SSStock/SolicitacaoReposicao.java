package projeto.business.SSStock;

import java.time.LocalDateTime;

public class SolicitacaoReposicao {
    private String idSolicitacao;
    private int quantidade;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataReposicaoPrevista;
    private LocalDateTime dataReposicao;
    private Ingrediente ingrediente;
    private String idStock;
    private String idPosto;

    public SolicitacaoReposicao(String idSolicitacao, int quantidade, String idStock, String idPosto, Ingrediente ingrediente) {
        this.idSolicitacao = idSolicitacao;
        this.ingrediente = ingrediente;
        this.dataReposicaoPrevista = null;
        this.dataReposicao = null;
        this.quantidade = quantidade;
        this.dataSolicitacao = LocalDateTime.now();
        this.idStock = idStock;
        this.idPosto = idPosto;
    }

    public SolicitacaoReposicao(String idSolicitacao,  int quantidade, LocalDateTime dataSolicitacao, LocalDateTime dataReposicaoPrevista, LocalDateTime dataReposicao, Ingrediente ingrediente, String idStock, String idPosto) {
        this.idSolicitacao = idSolicitacao;
        this.dataReposicaoPrevista = dataReposicaoPrevista;
        this.quantidade = quantidade;
        this.dataSolicitacao = dataSolicitacao;
        this.dataReposicao = dataReposicao;
        this.ingrediente = ingrediente;
        this.idStock = idStock;
        this.idPosto = idPosto;
    }

    public void atualizarDataReposicao(int minutos) {
        this.dataReposicaoPrevista = this.dataSolicitacao.plusMinutes((long) minutos);
    }

    public void finalizarReposicao() {
        this.dataReposicao = LocalDateTime.now();
    }

    public String getIdSolicitacao() {
        return this.idSolicitacao;
    }

    public Ingrediente getIngrediente() {
        return this.ingrediente;
    }

    public String getIdStock() {
        return this.idStock;
    }

    public String getIdPosto() {
        return this.idPosto;
    }

    public int getQuantidade() {
        return this.quantidade;
    }

    public boolean isConcluido() {
        return this.dataReposicao != null;
    }

    public int getMinutosAteReposicao() {
        LocalDateTime agora = LocalDateTime.now();
        if (agora.isAfter(this.dataReposicao)) {
            return 0;
        }
        long minutos = java.time.Duration.between(agora, this.dataReposicao).toMinutes();
        return (int) minutos;
    }

    public LocalDateTime getDataSolicitacao() {
        return this.dataSolicitacao;
    }

    public LocalDateTime getDataReposicao() {
        return this.dataReposicao;
    }

    public LocalDateTime getDataReposicaoPrevista() {
        return this.dataReposicaoPrevista;
    }
    
}
