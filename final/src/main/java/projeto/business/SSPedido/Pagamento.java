package projeto.business.SSPedido;

import java.time.LocalDateTime;

public class Pagamento {
	private float precoTotal;
	private float valorPago;
	private String metodoPagamento;
	private float troco;
	private LocalDateTime timestamp;
	private Fatura fatura;

	public Pagamento(float precoTotal, float valorPago, String metodoPagamento) throws IllegalArgumentException {
		this.precoTotal = precoTotal;
		this.valorPago = valorPago;
		this.troco = 0;
		this.timestamp = null;
		this.fatura = null;
		if (metodoPagamento.equalsIgnoreCase("multibanco") || metodoPagamento.equalsIgnoreCase("numerário") || metodoPagamento.equalsIgnoreCase("mbway")) {
			this.metodoPagamento = metodoPagamento.toLowerCase();
		} else {
			throw new IllegalArgumentException("Método de pagamento inválido.");
		}
	}


	public float getPrecoTotal() {
		return this.precoTotal;
	}

	public float getValorPago() {
		return this.valorPago;
	}

	public String getMetodoPagamento() {
		return this.metodoPagamento;
	}

	public float getTroco() {
		return this.troco;
	}

	public LocalDateTime getTimestamp() {
		return this.timestamp;
	}

	public Fatura getFatura() {
		return this.fatura == null ? null : this.fatura.clone();
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public void setFatura(Fatura fatura) {
		this.fatura = fatura;
	}

	public void setPrecoTotal(float precoTotal) {
		this.precoTotal = precoTotal;
	}

	public void setValorPago(float valorPago) {
		this.valorPago = valorPago;
	}

	public void setMetodoPagamento(String metodoPagamento) {
		this.metodoPagamento = metodoPagamento;
	}

	public void setTroco(float troco) {
		this.troco = troco;
	}

	
	public boolean confirmarPagamento() {
		boolean confirmado = this.precoTotal <= this.valorPago;
		if (confirmado) {
			setTroco(valorPago - precoTotal);
			this.timestamp = LocalDateTime.now();
			this.fatura = new Fatura(this.precoTotal, this.valorPago, this.metodoPagamento, this.troco);
		}
		return confirmado;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Preço Total: ").append(this.precoTotal).append("\n");
		sb.append("Valor Pago: ").append(this.valorPago).append("\n");
		sb.append("Método de Pagamento: ").append(this.metodoPagamento).append("\n");
		sb.append("Troco: ").append(this.troco).append("\n");
		sb.append("Timestamp: ").append(this.timestamp).append("\n");
		sb.append("Fatura: ").append(this.fatura != null ? this.fatura.toString() : "Nenhuma").append("\n");
		return sb.toString();
	}

	@Override
	public Pagamento clone() {
		Pagamento pagamentoClone = new Pagamento(this.precoTotal, this.valorPago, this.metodoPagamento);
		pagamentoClone.setTroco(this.troco);
		pagamentoClone.timestamp = this.timestamp;
		if (this.fatura != null) {
			pagamentoClone.fatura = this.fatura.clone();
		}
		return pagamentoClone;
	}

}
