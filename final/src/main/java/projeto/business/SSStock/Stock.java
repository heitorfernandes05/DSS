package projeto.business.SSStock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import projeto.data.StockIngredienteDAO;

public class Stock {
    private String idStock;
    private StockIngredienteDAO stocksIngredientes;
    private String idRestaurante;

    public Stock(String idStock, String idRestaurante) {
        this.idStock = idStock;
        this.idRestaurante = idRestaurante;
        this.stocksIngredientes = StockIngredienteDAO.getInstance();

    }

    public String getIdRestaurante() {
        return idRestaurante;
    }

    public String getIdStock() {
        return idStock;
    }  

    public int verificarStock(String idIng) throws IllegalArgumentException {
        if (!this.stocksIngredientes.containsKey(this.idStock, idIng)) {
            throw new IllegalArgumentException("Ingrediente inexistente no stock");
        }
        StockIngrediente si = this.stocksIngredientes.get(this.idStock, idIng);
        int res = si.getQuantidade();
        return res;
    }

    public void reporStock() {
        Collection<StockIngrediente> stocks = this.stocksIngredientes.values();
        for (StockIngrediente si : stocks) {
            si.reporStock();
            this.stocksIngredientes.put(idStock, si);
        }
    }

    public void reduzStockIngrediente(String idIng, int quantidade) throws IllegalArgumentException {
        StockIngrediente si = this.stocksIngredientes.get(this.idStock, idIng);
        si.reduzStock(quantidade);
        this.stocksIngredientes.put(this.idStock, si);
    }

    public void reporIngrediente(String idIng, int quantidade) throws IllegalArgumentException {
        StockIngrediente si = this.stocksIngredientes.get(this.idStock, idIng);
        si.aumentaStock(quantidade);
        this.stocksIngredientes.put(this.idStock, si);
    }

    public Ingrediente getIngrediente(String idIng) {
        StockIngrediente si = this.stocksIngredientes.get(this.idStock, idIng);
        Ingrediente ingrediente = si.getIngrediente();
        return ingrediente;
    }

    public List<StockIngrediente> getStockIngredientes() {
        Collection<StockIngrediente> stocks = this.stocksIngredientes.getByStock(idStock);
        List<StockIngrediente> res = new ArrayList<>(stocks);
        return res;
    }
    public List<Ingrediente> getIngredientes() {
        Collection<StockIngrediente> stocks = this.stocksIngredientes.getByStock(idStock);
        List<Ingrediente> res = new ArrayList<>();
        for (StockIngrediente si : stocks) {
            res.add(si.getIngrediente());
        }
        return res;
    }
    
}
