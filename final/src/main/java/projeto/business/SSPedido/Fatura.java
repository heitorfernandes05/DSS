package projeto.business.SSPedido;

public class Fatura {
    private String detalhes;

    public Fatura(float precoTotal, float valorPago, String metodoPagamento, float troco) {
        this.detalhes = "Total: " + precoTotal + "\nPago: " + valorPago + "\nMétodo: " + metodoPagamento + "\nTroco: " + troco;
    }

    public Fatura(String detalhes) {
        this.detalhes = detalhes;
    }

    public String getDetalhes() {
        return this.detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    @Override
    public String toString() {
        return this.detalhes;
    }

    @Override
    public Fatura clone() {
        return new Fatura(this.detalhes);
    }
}