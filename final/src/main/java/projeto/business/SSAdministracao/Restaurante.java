package projeto.business.SSAdministracao;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import projeto.data.MensagemDAO;

public class Restaurante {

    private String idRestaurante;
    private String local;
    private Indicadores indicadores;
    private MensagemDAO mensagens;

    public Restaurante(String id, String local) {
        this.idRestaurante = id;
        this.local = local;
        this.indicadores = new Indicadores();
        this.mensagens = MensagemDAO.getInstance();
    }

    public Restaurante(String id, String local2, Indicadores indicadores2) {
        this.idRestaurante = id;
        this.local = local2;
        this.indicadores = indicadores2;
        this.mensagens = MensagemDAO.getInstance();
    }

    public String getIdRestaurante() {
        return this.idRestaurante;
    }

    public String getLocal() {
        return this.local;
    }

    public Indicadores getIndicadores() {
        return this.indicadores;
    }

    public void atualizarIndicadores(double valorPedido, Duration tempoPedido) {
        this.indicadores.atualizarIndicadores(valorPedido, tempoPedido);
    }

    public double getFaturacao() {
        return this.indicadores.getFaturacao();
    }

    public Duration getTempoMedioAtendimento() {
        return this.indicadores.getTempoMedioAtendimento();
    }

    public void adicionarMensagem(Mensagem mensagem) {
        String idMensagem = "MSG-" + String.valueOf(this.mensagens.size() + 1);
        System.out.println("ID da mensagem: " + idMensagem);
        mensagem.setIdMensagem(idMensagem);
        this.mensagens.put(idMensagem, idRestaurante, mensagem);
    }

    public List<Mensagem> getMensagensAtivas() {
        Collection<Mensagem> r = this.mensagens.getMensagensAtivas(this.idRestaurante);
        List<Mensagem> res = new ArrayList<>(r);
        res.sort((m1, m2) -> m2.getTimestampEnvio().compareTo(m1.getTimestampEnvio()));
        return res;
    }

    public Restaurante clone() {
        Restaurante r = new Restaurante(this.idRestaurante, this.local);
        r.indicadores = this.indicadores;
        return r;
    }
    
}
