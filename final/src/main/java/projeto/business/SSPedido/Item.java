package projeto.business.SSPedido;

import java.util.List;
import java.util.ArrayList;
import projeto.business.SSStock.Ingrediente;

public class Item extends Produto {

    private List<Ingrediente> ingredientesItem;

    public Item(String idProduto, String nome,float precoBase, int tempoPreparacao, List<Ingrediente> ingredientesItem) {
        super(idProduto, nome);
        setPrecoBase(precoBase);
        setTempoPreparacao(tempoPreparacao);
        this.ingredientesItem = new ArrayList<>();
        for (Ingrediente ingrediente : ingredientesItem) {
            this.ingredientesItem.add((Ingrediente) ingrediente);
        }
    }

    public void addIngrediente(Ingrediente ingrediente) {
        if (ingrediente == null) {
            return;
        }
        this.ingredientesItem.add((Ingrediente) ingrediente);
    }

    public List<Ingrediente> getIngredientes() {
        List<Ingrediente> ingredientesClone = new ArrayList<>();
        for (Ingrediente ingrediente : this.ingredientesItem) {
            ingredientesClone.add((Ingrediente) ingrediente);
        }
        return ingredientesClone;
    }

    public void removeIngrediente(String idIngrediente) {
        this.ingredientesItem.removeIf(ingrediente -> ingrediente.getIdIngrediente().equals(idIngrediente));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Item ID: ").append(getId()).append(", ");
        sb.append("Nome: ").append(getNome()).append(", ");
        sb.append("Preço: ").append(getPrecoBase()).append("\n");

        sb.append("Ingredientes:\n");
        for (Ingrediente ingrediente : this.ingredientesItem) {
            sb.append(ingrediente.getIdIngrediente()).append("-").append(ingrediente.getNome()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public Item clone() {
        return new Item(this.getId(), this.getNome(), this.getPrecoBase(), this.getTempoPreparacao(), this.ingredientesItem);
    }

}