package projeto.business.SSAdministracao;

import java.time.Duration;

public class Indicadores {

    private double faturacao;
    private Duration tempoMedioAtendimento;
    private int numeroPedidos;

    public Indicadores() {
        this.faturacao = 0.0;
        this.tempoMedioAtendimento = Duration.ZERO;
        this.numeroPedidos = 0;
    }
    public Indicadores(double faturacao, Duration tempoMedioAtendimento, int numeroPedidos) {
        this.faturacao = faturacao;
        this.tempoMedioAtendimento = tempoMedioAtendimento;
        this.numeroPedidos = numeroPedidos;
    }
    public void atualizarIndicadores(double valorPedido, Duration tempoPedido) {
        long tempoMedioAtualSegundos = this.tempoMedioAtendimento.toSeconds();
        long novoTempoSegundos = tempoPedido.toSeconds();
        long novoTempoMedioSegundos = (tempoMedioAtualSegundos * this.numeroPedidos + novoTempoSegundos) 
                                    / (this.numeroPedidos + 1);
        this.tempoMedioAtendimento = Duration.ofSeconds(novoTempoMedioSegundos);
        this.faturacao += valorPedido;
        this.numeroPedidos ++;
    }

    public double getFaturacao() {
        return this.faturacao;
    }

    public Duration getTempoMedioAtendimento() {
        return this.tempoMedioAtendimento;
    }
    public int getNumPedidos() {
        return this.numeroPedidos;
    }
    
}
