package projeto.business.SSStock;

public class Ingrediente {
    private String idIngrediente;
    private String nome;
    private boolean alergenio;

    public Ingrediente(String idIngrediente, String nome, boolean alergenio) {
        this.idIngrediente = idIngrediente;
        this.nome = nome;
        this.alergenio = alergenio;
    }

    public String getIdIngrediente() {
        return idIngrediente;
    }
    public String getNome() {
        return nome;
    }

    public boolean isAlergenio() {
        return alergenio;
    }
}
