package projeto.business.SSPedido;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Pedido {
    private int numero;
    private float precoTotal;
    private int tempoPreparacao;
    private LocalDateTime timestamp;
    private String localEntrega;
    private String estado;
    private String nota;
    private Pagamento pagamento;
    private String restaurante;
    private List<Produto> produtosSelecionados;
    private int balcao;

    public Pedido(int numero, String idRestaurante) {
        this.numero = numero;
        this.precoTotal = 0.0f;
        this.tempoPreparacao = 0;
        this.timestamp = null;
        this.localEntrega = null;
        this.estado = atualizarEstado("iniciado");
        this.nota = null;
        this.pagamento = null;
        this.restaurante = idRestaurante;
        this.produtosSelecionados = new ArrayList<>();
        this.balcao = 0;
    }

    public int getTempoEstimado() {
        return this.calcularTempoEstimado();
    }

    public int setBalcao(int balcao) {
        return this.balcao = balcao;
    }

    public int getBalcao() {
        return this.balcao;
    }

    public int getNumero() {
        return this.numero;
    }

    public float getPrecoTotal() {
        return this.precoTotal;
    }

    public int getTempoPreparacao() {
        return this.tempoPreparacao;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getLocalEntrega() {
        return this.localEntrega;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNota() {
        return this.nota;
    }

    public String getRestaurante() {
        return this.restaurante;
    }

    public Pagamento getPagamento() {
        return this.pagamento == null ? null : this.pagamento.clone();
    }
    public List<Produto> getProdutosSelecionados() {
        List<Produto> produtosClone = new ArrayList<>();
        for (Produto p : this.produtosSelecionados) {
            produtosClone.add(p.clone());
        }
        return produtosClone;
    }

    public String produtosToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.produtosSelecionados.size(); i++) {
            sb.append("[").append(i).append("] ").append(this.produtosSelecionados.get(i).toString()).append("\n");
        }
        return sb.toString();
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setPrecoTotal(float precoTotal) {
        this.precoTotal = precoTotal;
    }

    public void setTempoPreparacao(int tempoPreparacao) {
        this.tempoPreparacao = tempoPreparacao;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setPagamento (Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public void setRestaurante(String restaurante) {
        this.restaurante = restaurante;
    }

    public void setProdutosSelecionados(List<Produto> produtosSelecionados) {
        this.produtosSelecionados = new ArrayList<>();
        for (Produto p : produtosSelecionados) {
            this.produtosSelecionados.add(p.clone());
        }
    }

    public boolean addProduto(Produto p) {
        if (p == null) {
            return false;
        }
        this.produtosSelecionados.add(p.clone());
        this.precoTotal += p.getPrecoBase();
        return true;
    }

    public void removeProduto(int idProdutoPedido) {
        if (this.produtosSelecionados.get(idProdutoPedido) != null) {
            this.precoTotal -= this.produtosSelecionados.get(idProdutoPedido).getPrecoBase();
            this.produtosSelecionados.remove(idProdutoPedido);
        }
    }

    public void removerIngrediente(int idProdutoPedido, int idItem, String idIngrediente) {
        Produto p = this.produtosSelecionados.get(idProdutoPedido);
        if (p != null) {
            if (p instanceof Menu menu){
                p = menu.getItem(idItem);
            }

            ((Item) p).removeIngrediente(idIngrediente);
        }
    }

    public void addNota(String texto) {
        this.nota = texto;
    }

    public void setLocalEntrega(String local) throws IllegalArgumentException {
        if (local.equalsIgnoreCase("local") || local.equalsIgnoreCase("takeaway")) {
            this.localEntrega = local.toLowerCase();
        } else {
            throw new IllegalArgumentException("Local de entrega inválido.");
        }
    }

    public String atualizarEstado(String novoEstado) throws IllegalArgumentException {
        List<String> estadosValidos = List.of("iniciado", "confirmado", "pago", "cancelado", "pagamento falhado", "em preparação", "concluído");
        if (estadosValidos.contains(novoEstado.toLowerCase())) {
            this.estado = novoEstado.toLowerCase();
        } else {
            throw new IllegalArgumentException("Estado inválido.");
        }
        return this.estado;
    }

    public void submeter() {
        this.timestamp = LocalDateTime.now();
        atualizarEstado("confirmado");
    }

    public void cancelar() {
        this.timestamp = LocalDateTime.now();
        this.precoTotal = 0;
        this.produtosSelecionados.clear();
        this.pagamento = null;
        this.nota = null;
        this.localEntrega = null;
        atualizarEstado("cancelado");
    }

    public int calcularTempoEstimado() {
        int tempoTotal = 0;
        for (Produto p : this.produtosSelecionados) {
            tempoTotal = Math.max(tempoTotal, p.getTempoPreparacao());
        }
        return tempoTotal+5;
    }
    
    public boolean pagar(float valorPago, String tipo) {
        setPagamento(new Pagamento(getPrecoTotal(), valorPago, tipo));
        boolean pago = this.pagamento.confirmarPagamento();
        if (pago) {
            atualizarEstado("pago");
            int balcao = new Random().nextInt(1, 4);
            setBalcao(balcao);
        }
        else { 
            setPagamento(null);
            atualizarEstado("pagamento falhado");
        }
        return pago;
    }

    public void atrasar(int tempoAtraso) {
        this.tempoPreparacao += tempoAtraso;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pedido Número: ").append(this.numero).append("\n");
        sb.append("Restaurante: ").append(this.restaurante).append("\n");
        sb.append("Preço Total: ").append(this.precoTotal).append("\n");
        sb.append("Tempo Preparação: ").append(this.tempoPreparacao).append(" minutos\n");
        sb.append("Timestamp: ").append(this.timestamp).append("\n");
        sb.append("Local Entrega: ").append(this.localEntrega).append("\n");
        sb.append("Estado: ").append(this.estado).append("\n");
        sb.append("Nota: ").append(this.nota).append("\n");
        sb.append("Produtos Selecionados:\n").append(produtosToString());
        sb.append("Pagamento: ").append(this.pagamento != null ? this.pagamento.toString() : "Nenhum").append("\n");
        return sb.toString();
    }

    @Override
    public Pedido clone() {
        Pedido pedidoClone = new Pedido(this.numero, this.restaurante);
        pedidoClone.setPrecoTotal(this.precoTotal);
        pedidoClone.setTempoPreparacao(this.tempoPreparacao);
        pedidoClone.setTimestamp(this.timestamp);
        pedidoClone.setLocalEntrega(this.localEntrega);
        pedidoClone.estado = this.estado;
        pedidoClone.nota = this.nota;
        if (this.pagamento != null) {
            pedidoClone.setPagamento(this.pagamento.clone());
        }
        pedidoClone.setProdutosSelecionados(this.produtosSelecionados);
        return pedidoClone;
    }

}
