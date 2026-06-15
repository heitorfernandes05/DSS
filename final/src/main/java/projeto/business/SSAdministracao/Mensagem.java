package projeto.business.SSAdministracao;

import java.time.LocalDateTime;

public class Mensagem {
    private String  idMensagem;
    private String conteudo;
    private User autor;
    private LocalDateTime timestampEnvio;
    private static final int validade= 1; // horas


    public Mensagem(User sessaoIniciada, String conteudo) {
        this.autor = sessaoIniciada;
        this.conteudo = conteudo;
        this.timestampEnvio = LocalDateTime.now();
    }

    public Mensagem(String  idMensagem, String conteudo, User autor, LocalDateTime timestampEnvio) {
        this. idMensagem =  idMensagem;
        this.conteudo = conteudo;
        this.autor = autor;
        this.timestampEnvio = timestampEnvio;
    }

    public String getIdMensagem() {
        return this. idMensagem;
    }

    public void setIdMensagem(String id) {
        this. idMensagem = id;
    }

    public LocalDateTime getTimestampEnvio() {
        return this.timestampEnvio;
    }
    
    public String getConteudo() {
        return this.conteudo;
    }

    public User getAutor() {
        return this.autor;
    }

    public boolean isAtiva() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime expiracao = this.timestampEnvio.plusHours(validade);
        return agora.isBefore(expiracao);
    }
    
}
