package projeto.business.SSPedido;

public abstract class Produto {
    private String idProduto;
    private String nome;
    private float precoBase;
    private int tempoPreparacao;

    public Produto(String idProduto, String nome) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.precoBase = 0;
        this.tempoPreparacao = 0;
    }

    public String getId() {
        return this.idProduto;
    }

    public String getNome() {
        return this.nome;
    }

    public float  getPrecoBase() {
        return this.precoBase;
    }

    public void setId(String idProduto) {
        this.idProduto = idProduto;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPrecoBase(float precoBase) {
        this.precoBase = precoBase;
    }

    public int getTempoPreparacao() {
        return this.tempoPreparacao;
    }

    public void setTempoPreparacao(int tempoPreparacao) {
        this.tempoPreparacao = tempoPreparacao;
    }

    public abstract Produto clone();
}
