package projeto.business.SSPedido;

import java.util.ArrayList;
import java.util.List;

public class Menu extends Produto {

    private float desconto;
    private List<Item> itensMenu;

    public Menu(String idProduto, String nome, float desconto, List<Item> itensMenu) {
        super(idProduto, nome);
        this.desconto = desconto;
        this.itensMenu = new ArrayList<>();
        for (Item item : itensMenu) {
            this.itensMenu.add(item.clone());
        }
        for (Item item : this.itensMenu) {
            setPrecoBase(getPrecoBase() + item.getPrecoBase());
            setTempoPreparacao(Math.max(item.getTempoPreparacao(), super.getTempoPreparacao()));
        }
        setPrecoBase(getPrecoBase() * (1 - desconto));
        this.desconto = calcularDesconto();
    }  
    
    private float calcularDesconto() {
        float totalItens = 0;
        for (Item item : this.itensMenu) {
            totalItens += item.getPrecoBase();
        }
        if (totalItens <= 0) {
            return 0;
        }
        float desconto = 1 - (this.getPrecoBase() / totalItens);
        if (desconto < 0) {
            return 0;
        }
        if (desconto > 1) {
            return 1;
        }
        return desconto;
    }

    public float getDesconto() {
        return this.desconto;
    }

    public List<Item> getItens() {
        return cloneItensMenu();
    }

    public void addItem(Item item) {
        if (item == null) {
            return;
        }
        this.itensMenu.add(item.clone());
    }
    
    public Item getItem(int idItem) {
        return this.itensMenu.get(idItem);
    }
    
    public void removeItem(String idItem) {
        this.itensMenu.removeIf(item -> item.getId().equals(idItem));
    }
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Menu ID: ").append(getId()).append(", ");
        sb.append("Nome: ").append(getNome()).append(", ");
        sb.append("Preço: ").append(getPrecoBase()).append(", ");
        sb.append("Itens:\n");
        for (Item item : this.itensMenu) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }
    
    public List<Item> cloneItensMenu() {
        List<Item> clonedItens = new ArrayList<>();
        for (Item item : this.itensMenu) {
            clonedItens.add(item.clone());
        }
        return clonedItens;
    }

    @Override
    public Menu clone() {
        return new Menu(this.getId(), this.getNome(),  this.desconto, this.cloneItensMenu());
    }

}
