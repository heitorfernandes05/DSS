package projeto.business.SSStock;

public class StockIngrediente {
    private Ingrediente ingrediente;
    private int quantidadeMin;
    private int quantidadeRec;
    private int quantidadeAtual;

    public StockIngrediente(Ingrediente ingrediente, int quantidadeMin, int quantidadeRec, int quantidadeAtual) {
        this.ingrediente = ingrediente;
        this.quantidadeMin = quantidadeMin;
        this.quantidadeRec = quantidadeRec;
        this.quantidadeAtual = quantidadeAtual;
    }

    public void reporStock() {
        this.quantidadeAtual = this.quantidadeRec;
    }

    public int getQuantidade() {
        return this.quantidadeAtual;
    }

    public int getQuantidadeMin() {
        return this.quantidadeMin;
    }

    public int getQuantidadeRec() {
        return this.quantidadeRec;
    }

    public void setQuantidade(int novaQuant) throws IllegalArgumentException {
        if(novaQuant < 0) {
            throw new IllegalArgumentException("Quantidade nao pode ser negativa");
        }
        this.quantidadeAtual = novaQuant;
    }

    public Ingrediente getIngrediente() {
        return this.ingrediente;
    }

    public void reduzStock(int quantidade) throws IllegalArgumentException {
        int novaQuant = this.quantidadeAtual - quantidade;
        if(novaQuant < 0) {
            throw new IllegalArgumentException("Quantidade nao pode ser negativa");
        }
        this.quantidadeAtual = novaQuant;
    }
    
    public void aumentaStock(int quantidade) throws IllegalArgumentException {
        int novaQuant = this.quantidadeAtual + quantidade;
        if (novaQuant < 0) {
            throw new IllegalArgumentException("Quantidade nao pode ser negativa");
        }
        this.quantidadeAtual = novaQuant;
    }

    public String toString() {
        return "Ingrediente: " + this.ingrediente.getIdIngrediente() + ", Quantidade Atual: " + this.quantidadeAtual +
               ", Quantidade Minima: " + this.quantidadeMin + ", Quantidade Recomendada: " + this.quantidadeRec;
    }
}
